package com.example.Kirana.service;

import com.example.Kirana.Kafka.producer.TransactionEventProducer;
import com.example.Kirana.dao.mongo.ProductDao;
import com.example.Kirana.dao.postgres.InventoryDao;
import com.example.Kirana.dao.postgres.TransactionDao;
import com.example.Kirana.dao.postgres.TransactionItemDao;
import com.example.Kirana.dto.request.RefundTransactionRequestDto;
import com.example.Kirana.dto.request.SaleTransactionRequestDto;
import com.example.Kirana.dto.request.TransactionItemRequestDto;
import com.example.Kirana.dto.response.TransactionResponseDto;
import com.example.Kirana.entity.mongo.Product;
import com.example.Kirana.entity.postgres.Transaction;
import com.example.Kirana.entity.postgres.TransactionItem;
import com.example.Kirana.enums.TransactionType;
import com.example.Kirana.dto.event.TransactionEvent;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.annotation.Propagation;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/**
 * ProductOrderService
 *
 * Handles sale and refund workflows.
 * Coordinates  products,inventory updates, transaction persistence,
 * and Kafka event publication.
 */
@Service
public class ProductOrderService {

    private final ProductDao productDao;
    private final InventoryDao inventoryDao;
    private final TransactionDao transactionDao;
    private final TransactionItemDao transactionItemDao;
    private final CurrencyService currencyService;
    private final TransactionEventProducer eventProducer;

    /**
     * Constructor-based dependency injection.
     */
    public ProductOrderService(ProductDao productDao,
                               InventoryDao inventoryDao,
                               TransactionDao transactionDao,
                               TransactionItemDao transactionItemDao,
                               CurrencyService currencyService,
                               TransactionEventProducer eventProducer) {
        this.productDao = productDao;
        this.inventoryDao = inventoryDao;
        this.transactionDao = transactionDao;
        this.transactionItemDao = transactionItemDao;
        this.currencyService = currencyService;
        this.eventProducer = eventProducer;
    }
    /**
     * Initiates a SALE transaction.
     *
     * High-level flow:
     *  1. Validate products & inventory (no DB mutation)
     *  2. Create transaction header (INITIATED)
     *  3. Process sale items atomically
     *
     * @param kiranaId Kirana ID
     * @param dto Sale request
     * @return Transaction response
     */
    public TransactionResponseDto sale(String kiranaId,SaleTransactionRequestDto dto) {

        // Validate all items first (no DB mutation)
        Map<String, Product> validatedProducts = new HashMap<>();

        for (TransactionItemRequestDto item : dto.getItems()) {

            //validate the products are present and the products are active
            Product product = productDao.findActiveById(item.getProductId());
            // Validate inventory capacity before starting transaction
            inventoryDao.validateStock(
                    product.getInventoryId(),
                    item.getQuantity()
            );
            // if both cases like capacity and product is active store in map so that no need to validate again
            validatedProducts.put(item.getProductId(), product);
        }
        //Create transaction header
        Transaction tx = new Transaction();
        tx.setKiranaId(kiranaId);
        tx.setCustomerId(dto.getCustomerId());
        tx.setType(TransactionType.SALE);
        tx.setStatus("INITIATED");
        tx.setCurrency(dto.getCurrency());
        tx.setAlreadyRefunded(false);

        transactionDao.save(tx);

        // 3Process items transactionally
        return processSaleItems(tx, dto, validatedProducts);
    }

    /**
     * Processes sale items atomically.
     *
     * Responsibilities:
     *  - Reduce inventory
     *  - Create transaction items
     *  - Calculate totals
     *  - Finalize transaction
     *  - Publish Kafka event
     */
    @Transactional
    protected TransactionResponseDto processSaleItems(Transaction tx, SaleTransactionRequestDto dto,
            Map<String, Product> products) {

        BigDecimal totalAmount = BigDecimal.ZERO;

        for (TransactionItemRequestDto item : dto.getItems()) {

            Product product = products.get(item.getProductId());

            // Reduce inventory
            inventoryDao.reduceStock(product.getInventoryId(), item.getQuantity());

            // INR base amount
            BigDecimal baseInr =
                    product.getUnitPrice()
                            .multiply(BigDecimal.valueOf(item.getQuantity()));

            // Convert to requested currency
            BigDecimal converted =
                    currencyService.convert(
                            baseInr,
                            "INR",
                            dto.getCurrency()
                    );

            totalAmount = totalAmount.add(converted);

            TransactionItem txItem = new TransactionItem();
            txItem.setTransactionId(tx.getId());
            txItem.setProId(product.getId());
            txItem.setProductName(product.getProName());
            txItem.setCategory(product.getCategory());
            txItem.setQuantity(item.getQuantity());
            txItem.setUnitPrice(product.getUnitPrice());
            txItem.setAmount(converted);

            transactionItemDao.save(txItem);
        }

        // Finalize transaction
        tx.setTotalAmount(totalAmount);
        tx.setExchangeRate(
                currencyService.convert(
                        BigDecimal.ONE,
                        "INR",
                        tx.getCurrency()
                )
        );
        tx.setStatus("COMPLETED");

        transactionDao.save(tx);

        // Publish Kafka event
        publishEvent(tx, "SALE", totalAmount);

        return new TransactionResponseDto(
                tx.getId(),
                "SUCCESS",
                "Sale successful"
        );
    }
    /**
     * Initiates a REFUND transaction.
     *
     * Flow:
     *  1. Validate original transaction
     *  2. Create refund transaction header
     *  3. Restore inventory + create refund items
     *  4. Mark original transaction refunded
     *  5. Publish Kafka event
     */
    @Transactional
    public TransactionResponseDto refund(RefundTransactionRequestDto dto) {

        Transaction original = validateRefundRequest(dto);
        Transaction refund = new Transaction();
        createRefundTransactionHeader(refund, original);
        BigDecimal refundTotal =processRefundItems(refund, original);;

        // Mark original transaction as refunded
        original.setAlreadyRefunded(true);
        transactionDao.save(original);

        // Update the state
        refund.setStatus("COMPLETED");
        refund.setTotalAmount(refundTotal);
        transactionDao.save(refund);

        // Publish Kafka event
        publishEvent(refund, "REFUND", refundTotal);

        return new TransactionResponseDto(
                refund.getId(),
                "SUCCESS",
                "Refund successful"
        );
    }
    /**
     * Processes refund items atomically.
     *
     * Must run inside an existing transaction.
     * Restores inventory and creates refund line items.
     */
    @Transactional(propagation = Propagation.MANDATORY)
    protected BigDecimal processRefundItems(Transaction refundTx, Transaction originalTx) {

        List<TransactionItem> originalItems =
                transactionItemDao.findByTransactionId(originalTx.getId());

        BigDecimal refundTotal = BigDecimal.ZERO;

        for (TransactionItem oldItem : originalItems) {

            Product product = productDao.findActiveById(oldItem.getProId());
            // Restore inventory
            inventoryDao.addStock(
                    product.getInventoryId(),
                    oldItem.getQuantity()
            );

            BigDecimal refundAmount =
                    oldItem.getAmount();

            refundTotal = refundTotal.add(refundAmount);

            TransactionItem refundItem = new TransactionItem();
            refundItem.setTransactionId(refundTx.getId());
            refundItem.setProId(oldItem.getProId());
            refundItem.setProductName(oldItem.getProductName());
            refundItem.setCategory(oldItem.getCategory());
            refundItem.setQuantity(oldItem.getQuantity());
            refundItem.setUnitPrice(oldItem.getUnitPrice());
            refundItem.setAmount(refundAmount);
            transactionItemDao.save(refundItem);
        }

        return refundTotal;
    }


    /**
     * Creates refund transaction header using original transaction data.
     */
    private void createRefundTransactionHeader(Transaction refund, Transaction original) {
        refund.setKiranaId(original.getKiranaId());
        refund.setCustomerId(original.getCustomerId());
        refund.setType(TransactionType.REFUND);
        refund.setStatus("INITIATED");
        refund.setCurrency(original.getCurrency());
        refund.setExchangeRate(original.getExchangeRate());
        refund.setAlreadyRefunded(false);
        refund.setOriginalTransactionId(original.getId());
        transactionDao.save(refund);
    }
    /**
     * Validates refund eligibility.
     *
     * Prevents duplicate refunds.
     */
    private Transaction validateRefundRequest(RefundTransactionRequestDto dto) {
        Transaction original =
                transactionDao.findById(dto.getOriginalTransactionId());
        //Prevents duplicate Refunds
        if (original.isAlreadyRefunded()) {
            throw new RuntimeException("Transaction already refunded");
        }

        return original;
    }
    /**
     * Publishes transaction events to Kafka.
     *
     * Used for:
     *  - Financial reporting
     */
    private void publishEvent(Transaction tx, String type,
                              BigDecimal amount) {
        TransactionEvent event = new TransactionEvent();
        event.setTransactionId(tx.getId());
        event.setKiranaId(tx.getKiranaId());
        event.setType(type);
        event.setAmount(amount);
        event.setCurrency(tx.getCurrency());
        event.setCreatedAt(new Date());
        eventProducer.publish(event);
    }
}

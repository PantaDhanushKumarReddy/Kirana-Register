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
import com.github.f4b6a3.ulid.UlidCreator;
import io.jsonwebtoken.Claims;
import jakarta.transaction.Transactional;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ProductOrderService {

    private final ProductDao productDao;
    private final InventoryDao inventoryDao;
    private final TransactionDao transactionDao;
    private final TransactionItemDao transactionItemDao;
    private final CurrencyService currencyService;
    private final TransactionEventProducer eventProducer;

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
    public TransactionResponseDto sale(SaleTransactionRequestDto dto) {

        // 1️VALIDATE ALL ITEMS FIRST (NO TRANSACTION)
        Map<String, Product> validatedProducts = new HashMap<>();

        for (TransactionItemRequestDto item : dto.getItems()) {
            Product product = productDao.findActiveById(item.getProductId());
            inventoryDao.validateStock(
                    product.getInventoryId(),
                    item.getQuantity()
            );
            validatedProducts.put(item.getProductId(), product);
        }

        // 2️⃣ CREATE TRANSACTION (INITIATED)
        Claims claims = (Claims) SecurityContextHolder
                .getContext().getAuthentication().getDetails();

        String transactionId = UlidCreator.getUlid().toString();

        Transaction tx = new Transaction();
        tx.setId(transactionId);
        tx.setKId(claims.get("kId", String.class));
        tx.setCustomerId(dto.getCustomerId());
        tx.setType(TransactionType.SALE);
        tx.setStatus("INITIATED");
        tx.setCurrency(dto.getCurrency());
        tx.setAlreadyRefunded(false);
        tx.setCreated_at(Instant.now());
        tx.setUpdated_at(Instant.now());

        transactionDao.save(tx);

        // 3️PROCESS ITEMS (TRANSACTIONAL)
        return processSaleItems(tx, dto, validatedProducts);
    }

    @Transactional
    protected TransactionResponseDto processSaleItems(
            Transaction tx,
            SaleTransactionRequestDto dto,
            Map<String, Product> products) {

        BigDecimal totalAmount = BigDecimal.ZERO;

        for (TransactionItemRequestDto item : dto.getItems()) {

            Product product = products.get(item.getProductId());

            // Reduce inventory
            inventoryDao.reduceStock(
                    product.getInventoryId(),
                    item.getQuantity()
            );

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
            txItem.setTransactionItemId(UlidCreator.getUlid().toString());
            txItem.setTransactionId(tx.getId());
            txItem.setProId(product.getId());
            txItem.setProductName(product.getProName());
            txItem.setCategory(product.getCategory());
            txItem.setQuantity(item.getQuantity());
            txItem.setUnitPrice(product.getUnitPrice());
            txItem.setAmount(converted);
            txItem.setCreatedAt(Instant.now());

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
        tx.setUpdated_at(Instant.now());

        transactionDao.save(tx);

        // PUBLISH KAFKA EVENT
        publishEvent(tx, "SALE", totalAmount);

        return new TransactionResponseDto(
                tx.getId(),
                "SUCCESS",
                "Sale successful"
        );
    }
    @Transactional
    public TransactionResponseDto refund(RefundTransactionRequestDto dto) {

        Transaction original =
                transactionDao.findById(dto.getOriginalTransactionId());

        //  SAFETY: Prevent double refund
        if (original.isAlreadyRefunded()) {
            throw new RuntimeException("Transaction already refunded");
        }

        List<TransactionItem> originalItems =
                transactionItemDao.findByTransactionId(
                        original.getId()
                );

        String refundTransactionId =
                UlidCreator.getUlid().toString();

        BigDecimal refundTotal = BigDecimal.ZERO;

        for (TransactionItem oldItem : originalItems) {

            Product product =
                    productDao.findActiveById(oldItem.getProId());

            // Restore inventory
            inventoryDao.addStock(
                    product.getInventoryId(),
                    oldItem.getQuantity()
            );

            BigDecimal refundAmount =
                    oldItem.getAmount().negate();

            refundTotal = refundTotal.add(refundAmount);

            TransactionItem refundItem = new TransactionItem();
            refundItem.setTransactionItemId(
                    UlidCreator.getUlid().toString());
            refundItem.setTransactionId(refundTransactionId);
            refundItem.setProId(oldItem.getProId());
            refundItem.setProductName(oldItem.getProductName());
            refundItem.setCategory(oldItem.getCategory());
            refundItem.setQuantity(oldItem.getQuantity());
            refundItem.setUnitPrice(oldItem.getUnitPrice());
            refundItem.setAmount(refundAmount);
            refundItem.setCreatedAt(Instant.now());

            transactionItemDao.save(refundItem);
        }

        // Mark original transaction as refunded
        original.setAlreadyRefunded(true);
        original.setUpdated_at(Instant.now());
        transactionDao.save(original);

        // Create refund transaction
        Transaction refund = new Transaction();
        refund.setId(refundTransactionId);
        refund.setKId(original.getKId());
        refund.setCustomerId(original.getCustomerId());
        refund.setType(TransactionType.REFUND);
        refund.setStatus("COMPLETED");
        refund.setCurrency(original.getCurrency());
        refund.setExchangeRate(original.getExchangeRate());
        refund.setTotalAmount(refundTotal);
        refund.setOriginalTransactionId(
                original.getId()
        );
        refund.setAlreadyRefunded(false);
        refund.setCreated_at(Instant.now());
        refund.setUpdated_at(Instant.now());

        transactionDao.save(refund);

        // PUBLISH KAFKA EVENT
//        publishEvent(refund, "REFUND", refundTotal.abs());

        return new TransactionResponseDto(
                refundTransactionId,
                "SUCCESS",
                "Refund successful"
        );
    }
    private void publishEvent(Transaction tx,
                              String type,
                              BigDecimal amount) {

        TransactionEvent event = new TransactionEvent();
        event.setTransactionId(tx.getId());
        event.setKId(tx.getKId());
        event.setType(type);
        event.setAmount(amount);
        event.setCurrency(tx.getCurrency());
        event.setCreatedAt(Instant.now());

        eventProducer.publish(event);
    }
}

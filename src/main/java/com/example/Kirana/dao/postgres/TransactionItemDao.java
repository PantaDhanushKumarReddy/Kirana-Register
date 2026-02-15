package com.example.Kirana.dao.postgres;

import com.example.Kirana.entity.postgres.TransactionItem;
import com.example.Kirana.repository.postgres.TransactionItemRepository;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TransactionItemDao {

    private TransactionItemRepository repository;
    /**
     * Constructor-based dependency injection.
     *
     * @param repository TransactionItem repository
     */
    public TransactionItemDao(TransactionItemRepository repository) {
        this.repository = repository;
    }
    /**
     * Saves a transaction item.
     *
     * @param item Transaction item entity
     * @return Persisted transaction item
     */
    public TransactionItem save(TransactionItem item) {
        return repository.save(item);
    }
    /**
     * Finds all transaction items for a given transaction ID.
     *
     * @param transactionId Transaction ID
     * @return List of transaction items
     */
    public List<TransactionItem> findByTransactionId(String transactionId) {
        return repository.findByTransactionId(transactionId);
    }
}

package com.example.Kirana.dao.postgres;

import com.example.Kirana.entity.postgres.TransactionItem;
import com.example.Kirana.repository.postgres.TransactionItemRepository;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TransactionItemDao {

    private TransactionItemRepository repository;
    public TransactionItemDao(TransactionItemRepository repository) {
        this.repository = repository;
    }

    public TransactionItem save(TransactionItem item) {
        return repository.save(item);
    }
    public List<TransactionItem> findByTransactionId(String transactionId) {
        return repository.findByTransactionId(transactionId);
    }
}

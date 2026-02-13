package com.example.Kirana.dao.postgres;

import com.example.Kirana.entity.postgres.Transaction;
import com.example.Kirana.repository.postgres.TransactionRepository;
import org.springframework.stereotype.Component;

@Component
public class TransactionDao {
    private TransactionRepository transactionRepository;

    public TransactionDao(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }
    public Transaction save(Transaction transaction){
        return transactionRepository.save(transaction);
    }
    public Transaction findById(String id) {
        return transactionRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Transaction not found: " + id));
    }
}

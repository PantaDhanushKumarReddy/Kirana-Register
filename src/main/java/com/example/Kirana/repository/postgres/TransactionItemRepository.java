package com.example.Kirana.repository.postgres;

import com.example.Kirana.entity.postgres.TransactionItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransactionItemRepository extends JpaRepository<TransactionItem,String> {
    List<TransactionItem> findByTransactionId(String transactionId);
}

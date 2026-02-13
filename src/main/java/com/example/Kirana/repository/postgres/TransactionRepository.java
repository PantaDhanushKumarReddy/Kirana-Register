package com.example.Kirana.repository.postgres;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.Kirana.entity.postgres.Transaction;
public interface TransactionRepository extends JpaRepository<Transaction,String> {
}

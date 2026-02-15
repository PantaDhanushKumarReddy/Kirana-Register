package com.example.Kirana.repository.postgres;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.Kirana.entity.postgres.Transaction;
/**
 * TransactionRepository
 *
 * JPA repository for managing Transaction entities stored in PostgreSQL.
 */
public interface TransactionRepository extends JpaRepository<Transaction,String> {
}

package com.example.Kirana.repository.postgres;

import com.example.Kirana.entity.postgres.TransactionItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
/**
 * TransactionItemRepository
 *
 * JPA repository for managing TransactionItem entities.
 * Used to fetch individual product line items for a transaction.
 */
public interface TransactionItemRepository extends JpaRepository<TransactionItem,String> {
    /**
     * Finds all transaction items associated with a given transaction.
     *
     * Used for:
     *  - Viewing transaction details
     *  - Refund processing
     *  - Financial reporting and audits
     *
     * @param transactionId Parent transaction ID
     * @return List of transaction items
     */
    List<TransactionItem> findByTransactionId(String transactionId);
}

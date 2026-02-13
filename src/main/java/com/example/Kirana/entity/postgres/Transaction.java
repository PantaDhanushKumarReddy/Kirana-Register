package com.example.Kirana.entity.postgres;

import com.example.Kirana.enums.TransactionType;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;
/**
 * Transaction Entity
 *
 * Represents a financial transaction performed by a Kirana store.
 * Supports multiple currencies, refunds, and exchange rate tracking.
 * Stored in PostgreSQL for strong consistency and reporting.
 */
@Entity
@Table(name = "transaction")
@Data
public class Transaction {
    @Id
    private String id;
    @Column(name = "k_id", nullable = false)
    private String kId;
    @Column(name = "customer_id")
    private String customerId;
    @Enumerated(EnumType.STRING)
    private TransactionType type;
    private String currency;
    @Column(name = "total_amount")
    private BigDecimal totalAmount;
    @Column(name = "exchange_rate")
    private BigDecimal exchangeRate;
    /**
     * Reference to the original transaction ID.
     * Used in refund or reversal scenarios.
     */
    @Column(name = "original_transaction_id")
    private String originalTransactionId;
    private String status;
    /**
     * Indicates whether this transaction has already been refunded.
     * Prevents duplicate refunds.
     */
    private boolean alreadyRefunded;
    private Instant created_at;
    private Instant updated_at;
}

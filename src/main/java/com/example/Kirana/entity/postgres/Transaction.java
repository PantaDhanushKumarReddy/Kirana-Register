package com.example.Kirana.entity.postgres;

import com.example.Kirana.enums.TransactionType;
import com.github.f4b6a3.ulid.UlidCreator;
import jakarta.persistence.*;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.util.Date;

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
@EntityListeners(AuditingEntityListener.class) //Because of we are using createdDate and LastModifiedDate annotations
public class Transaction {
    @Id
    private String id;
    @Column(name = "kirana_id", nullable = false)
    private String kiranaId;
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
    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private Date createdAt;
    @LastModifiedDate
    @Column(name = "updated_at")
    private Date updatedAt;
    /**
     * Auto-generate ULID.
     */
    @PrePersist
    public void generateId() {
        this.id = UlidCreator.getUlid().toString();
    }
}

package com.example.Kirana.entity.postgres;

import com.github.f4b6a3.ulid.UlidCreator;
import jakarta.persistence.*;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Date;

/**
 * TransactionItem Entity
 *
 * Represents an individual product line item within a transaction.
 * Each record corresponds to one product sold/refunded in a transaction.
 * Stored in PostgreSQL for accurate financial reporting.
 */
@Entity
@Table(name = "transaction_item")
@Data
@EntityListeners(AuditingEntityListener.class)
public class TransactionItem {

    @Id
    @Column(name = "transaction_item_id")
    private String transactionItemId;

    @Column(name = "transaction_id", nullable = false)
    private String transactionId;

    @Column(name = "pro_id")
    private String proId;

    private String productName;
    private String category;

    private int quantity;

    @Column(name = "unit_price")
    private BigDecimal unitPrice;   // INR price

    private BigDecimal amount;      // converted amount
    @CreatedDate
    private Date createdAt;
    @PrePersist
    public void generateId() {
        this.transactionItemId = UlidCreator.getUlid().toString();
    }
}

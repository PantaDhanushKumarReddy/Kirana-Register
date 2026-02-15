package com.example.Kirana.entity.postgres;

import com.github.f4b6a3.ulid.UlidCreator;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.util.Date;

/**
 * Inventory Entity
 *
 * Represents inventory details for a product.
 * Stored in PostgreSQL and used to track stock levels and capacity.
 */
@Data
@Entity
@Table(name = "inventory")
@EntityListeners(AuditingEntityListener.class)
public class Inventory {

    @Id
    private String id; // ULID

    /**
     * Current available quantity of the product.
     * Must be zero or a positive value.
     */
    @Min(0)
    private int quantity;
    /**
     * Maximum storage capacity for the product.
     * Must be at least 1.
     */
    @Min(1)
    private int capacity;
    @CreatedDate
    @Column(updatable = false)
    private Date createdAt;

    @LastModifiedDate
    private Date updatedAt;
    @PrePersist
    public void generateId() {
        this.id = UlidCreator.getUlid().toString();
    }
}

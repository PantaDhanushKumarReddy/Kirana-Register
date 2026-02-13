package com.example.Kirana.entity.postgres;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import lombok.Data;
import java.time.Instant;
/**
 * Inventory Entity
 *
 * Represents inventory details for a product.
 * Stored in PostgreSQL and used to track stock levels and capacity.
 */
@Data
@Entity
@Table(name = "inventory")
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

    private Instant updatedAt;
}

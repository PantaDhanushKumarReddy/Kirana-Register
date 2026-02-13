package com.example.Kirana.entity.mongo;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.math.BigDecimal;
import java.time.Instant;
/**
 * Product Entity
 *
 * Represents a product sold by a Kirana store.
 * Stored as a MongoDB document and linked to a Kirana and Inventory record with
 * their respective Ids.
 */
@Data
@Document(collection = "product")
public class Product {

    @Id
    private String id; // ULID

    private String kId;  //KiranaStoreId
    private String inventoryId;

    @NotBlank
    private String proName;

    @NotBlank
    private String category;

    @NotBlank
    private String description;

    @NotNull
    @Positive
    private BigDecimal unitPrice;

    private String currency;

    private boolean isActive;

    private Instant createdAt;
    private Instant updatedAt;
}

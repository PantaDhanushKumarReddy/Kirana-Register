package com.example.Kirana.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;
/**
 * ProductRequestDto
 *
 * Request DTO used for creating a new product.
 * Contains product details along with initial inventory configuration.
 */
@Data
public class ProductRequestDto {

    @NotBlank
    private String proName;

    @NotBlank
    private String category;

    @NotBlank
    private String description;

    @NotNull
    @Positive
    private BigDecimal unitPrice;

    @Min(value = 0, message = "Initial quantity cannot be negative")
    private int initialQuantity;

    @Min(value = 1, message = "Capacity must be at least 1")
    private int capacity;
}
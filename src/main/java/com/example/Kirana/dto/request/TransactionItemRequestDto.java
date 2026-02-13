package com.example.Kirana.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
/**
 * TransactionItemRequestDto
 *
 * Request DTO representing a single item.
 */
@Data
public class TransactionItemRequestDto {

    @NotBlank
        private String productId;

    @Min(1)
    private int quantity;
}

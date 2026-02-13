package com.example.Kirana.dto.request;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;
/**
 * SaleTransactionRequestDto
 *
 * Request DTO used to create a sale transaction.
 * Contains customer details, currency, and purchased items.
 */
@Data
public class SaleTransactionRequestDto {

    private String customerId;

    @NotBlank
    private String currency;

    @NotEmpty
    private List<TransactionItemRequestDto> items;
}

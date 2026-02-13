package com.example.Kirana.dto.request;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;
/**
 * RefundTransactionRequestDto
 *
 * Request DTO used to initiate a refund for an existing transaction.
 */
@Data
public class RefundTransactionRequestDto {

    @NotBlank
    private String originalTransactionId;
}

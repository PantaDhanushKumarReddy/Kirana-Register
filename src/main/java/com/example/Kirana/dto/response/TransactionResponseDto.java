package com.example.Kirana.dto.response;
import lombok.AllArgsConstructor;
import lombok.Data;
/**
 * TransactionResponseDto
 *
 * Response DTO returned after processing a transaction
 * (sale, refund, or reversal) and the status like processing or completed.
 */
@Data
@AllArgsConstructor
public class TransactionResponseDto {
    private String transactionId;
    private String status;
    private String message;
}

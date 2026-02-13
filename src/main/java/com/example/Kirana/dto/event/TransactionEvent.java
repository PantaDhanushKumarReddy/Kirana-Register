package com.example.Kirana.dto.event;

import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;

@Data
public class TransactionEvent {

    private String transactionId;
    private String kId;

    private String type; // SALE | REFUND
    private BigDecimal amount;
    private String currency;

    private Instant createdAt;
}
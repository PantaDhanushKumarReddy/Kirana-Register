package com.example.Kirana.dto.event;

import lombok.Data;
import org.springframework.data.annotation.CreatedDate;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Date;

@Data
public class TransactionEvent {

    private String transactionId;
    private String kiranaId;

    private String type; // SALE | REFUND
    private BigDecimal amount;
    private String currency;
    private Date createdAt;
}
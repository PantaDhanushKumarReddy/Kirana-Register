package com.example.Kirana.entity.mongo;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.Instant;


/**
 * FinancialReport
 *
 * Aggregated financial summary for a Kirana store over a fixed time period.
 *
 * This document is designed for:
 *  - reporting (weekly / monthly / yearly)
 *
 * Instead of recalculating totals from transactions every time,
 * we store pre-aggregated values for fast reads.
 *
 * Stored in MongoDB because:
 *  - schema is stable but read-heavy
 *  - analytics queries are flexible
 *  - no transactional guarantees are required
 */
@Data
@Document(collection = "financial_reports")
public class FinancialReport {

    @Id
    private String id; // ULID

    private String kId;

    private String periodType; // WEEK | MONTH | YEAR
    private String periodKey;  // 2026-W07 | 2026-02 | 2026

    private String currency;

    private BigDecimal totalCredit; // SALE
    private BigDecimal totalDebit;  // REFUND
    private BigDecimal netFlow;

    private Instant updatedAt;
}

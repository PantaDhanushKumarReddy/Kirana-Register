package com.example.Kirana.entity.mongo;

import com.example.Kirana.enums.ReportPeriodType;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Date;


/**
 * FinancialReport
 *
 * Aggregated financial summary for a Kirana store.
 * Stored as pre-computed data for fast reporting.
 */
@Data
@Document(collection = "financial_reports")
public class FinancialReport {

    @Id
    private String id;// Mongo ObjectId (auto-generated)

    private String kiranaId;


    private ReportPeriodType periodType; // WEEK | MONTH | YEAR
    /**
     * User-friendly period key:
     * WEEK  → 2026-W07
     * MONTH → Feb-2026
     * YEAR  → 2026
     */
    private String periodKey;

    private String currency;

    private BigDecimal totalCredit; // SALE
    private BigDecimal totalDebit;  // REFUND
    private BigDecimal netFlow;
    @LastModifiedDate
    private Date updatedAt;
}

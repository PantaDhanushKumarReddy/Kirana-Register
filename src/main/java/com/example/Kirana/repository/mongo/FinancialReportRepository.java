package com.example.Kirana.repository.mongo;

import com.example.Kirana.entity.mongo.FinancialReport;
import com.example.Kirana.enums.ReportPeriodType;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

/**
 * FinancialReportRepository
 *
 * MongoDB repository for managing FinancialReport documents.
 * Used to fetch and persist aggregated financial data for reporting.
 */
public interface FinancialReportRepository
        extends MongoRepository<FinancialReport, String> {
    /**
     * Finds a financial report for a specific Kirana, time period, and currency.
     *
     * Used to:
     *  - Fetch existing report before updating aggregates
     *  - Avoid duplicate financial reports for the same period
     *
     * @param kiranaId        Kirana ID
     * @param periodType Period type (e.g., DAILY, MONTHLY)
     * @param periodKey  Period identifier (e.g., 2026-02-13, 2026-02)
     * @param currency   Currency code (e.g., INR, USD)
     * @return Optional FinancialReport if present
     */
    Optional<FinancialReport>
    findByKiranaIdAndPeriodTypeAndPeriodKeyAndCurrency(
            String kiranaId,
            ReportPeriodType periodType,
            String periodKey,
            String currency
    );
}

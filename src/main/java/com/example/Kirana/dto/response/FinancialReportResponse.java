package com.example.Kirana.dto.response;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
/**
 * FinancialReportResponse
 *
 * Response DTO representing aggregated financial data for a Kirana store.
 */
@Data
@AllArgsConstructor
public class FinancialReportResponse {
    private BigDecimal totalCredit;
    private BigDecimal totalDebit;
    private BigDecimal netFlow;
}

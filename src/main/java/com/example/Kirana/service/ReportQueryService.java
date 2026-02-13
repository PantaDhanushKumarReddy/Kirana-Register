package com.example.Kirana.service;
import com.example.Kirana.dto.response.FinancialReportResponse;
import com.example.Kirana.entity.mongo.FinancialReport;
import com.example.Kirana.repository.mongo.FinancialReportRepository;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class ReportQueryService {


    private FinancialReportRepository repository;
    public ReportQueryService(FinancialReportRepository repository) {
        this.repository = repository;
    }

    public FinancialReportResponse get(
            String periodType,
            String periodKey,
            String currency) {

        Claims claims = (Claims) SecurityContextHolder
                .getContext().getAuthentication().getDetails();

        String kId = claims.get("kId", String.class);

        FinancialReport report =
                repository.findBykIdAndPeriodTypeAndPeriodKeyAndCurrency(
                        kId, periodType, periodKey, currency
                ).orElseThrow(() ->
                        new RuntimeException("Report not found"));

        return new FinancialReportResponse(
                report.getTotalCredit(),
                report.getTotalDebit(),
                report.getNetFlow()
        );
    }
}

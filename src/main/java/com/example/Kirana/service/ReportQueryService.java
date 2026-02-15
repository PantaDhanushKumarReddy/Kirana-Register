package com.example.Kirana.service;

import com.example.Kirana.dao.mongo.FinancialReportDao;
import com.example.Kirana.dto.response.FinancialReportResponse;
import com.example.Kirana.entity.mongo.FinancialReport;
import com.example.Kirana.enums.ReportPeriodType;
import io.jsonwebtoken.Claims;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class ReportQueryService {

    private final FinancialReportDao dao;

    public ReportQueryService(FinancialReportDao dao) {
        this.dao = dao;
    }

    public FinancialReportResponse get(
            ReportPeriodType periodType,
            String periodKey,
            String currency
    ) {

        Claims claims = (Claims)
                SecurityContextHolder.getContext()
                        .getAuthentication()
                        .getDetails();

        String kiranaId = claims.get("kiranaId", String.class);

        FinancialReport report =
                dao.findOrCreate(
                        kiranaId,
                        periodType,
                        periodKey,
                        currency
                );

        return new FinancialReportResponse(
                report.getTotalCredit(),
                report.getTotalDebit(),
                report.getNetFlow()
        );
    }
}

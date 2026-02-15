package com.example.Kirana.dao.mongo;
import com.example.Kirana.entity.mongo.FinancialReport;
import com.example.Kirana.enums.ReportPeriodType;
import com.example.Kirana.repository.mongo.FinancialReportRepository;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Date;

@Component
public class FinancialReportDao {

    private final FinancialReportRepository repository;

    public FinancialReportDao(FinancialReportRepository repository) {
        this.repository = repository;
    }

    public FinancialReport findOrCreate(
            String kiranaId,
            ReportPeriodType type,
            String periodKey,
            String currency
    ) {
        return repository
                .findByKiranaIdAndPeriodTypeAndPeriodKeyAndCurrency(
                        kiranaId, type, periodKey, currency
                )
                .orElseGet(() -> {
                    FinancialReport r = new FinancialReport();
                    r.setKiranaId(kiranaId);
                    r.setPeriodType(type);
                    r.setPeriodKey(periodKey);
                    r.setCurrency(currency);
                    r.setTotalCredit(BigDecimal.ZERO);
                    r.setTotalDebit(BigDecimal.ZERO);
                    r.setNetFlow(BigDecimal.ZERO);
                    r.setUpdatedAt(new Date());
                    return r;
                });
    }

    public FinancialReport save(FinancialReport report) {
        report.setUpdatedAt(new Date());
        return repository.save(report);
    }
}

package com.example.Kirana.service;
import com.example.Kirana.dto.event.TransactionEvent;
import com.example.Kirana.entity.mongo.FinancialReport;
import com.example.Kirana.repository.mongo.FinancialReportRepository;
import com.github.f4b6a3.ulid.UlidCreator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class ReportAggregationService {

    private FinancialReportRepository repository;

    public ReportAggregationService(FinancialReportRepository repository) {
        this.repository = repository;
    }

    public void aggregate(TransactionEvent event) {

        List<Period> periods = List.of(
                Period.week(event.getCreatedAt()),
                Period.month(event.getCreatedAt()),
                Period.year(event.getCreatedAt())
        );

        for (Period p : periods) {

            FinancialReport report =
                    repository.findBykIdAndPeriodTypeAndPeriodKeyAndCurrency(
                            event.getKId(),
                            p.type,
                            p.key,
                            event.getCurrency()
                    ).orElseGet(() -> {
                        FinancialReport r = new FinancialReport();
                        r.setId(UlidCreator.getUlid().toString());
                        r.setKId(event.getKId());
                        r.setPeriodType(p.type);
                        r.setPeriodKey(p.key);
                        r.setCurrency(event.getCurrency());
                        r.setTotalCredit(BigDecimal.ZERO);
                        r.setTotalDebit(BigDecimal.ZERO);
                        r.setNetFlow(BigDecimal.ZERO);
                        return r;
                    });

            if ("SALE".equals(event.getType())) {
                report.setTotalCredit(
                        report.getTotalCredit().add(event.getAmount()));
            } else {
                report.setTotalDebit(
                        report.getTotalDebit().add(event.getAmount()));
            }

            report.setNetFlow(
                    report.getTotalCredit()
                            .subtract(report.getTotalDebit())
            );

            report.setUpdatedAt(Instant.now());
            repository.save(report);
        }
    }

    // helper record
    private record Period(String type, String key) {

        static Period week(Instant i) {
            String key = DateTimeFormatter.ofPattern("YYYY-'W'ww")
                    .withZone(ZoneId.of("UTC"))
                    .format(i);
            return new Period("WEEK", key);
        }

        static Period month(Instant i) {
            String key = DateTimeFormatter.ofPattern("yyyy-MM")
                    .withZone(ZoneId.of("UTC"))
                    .format(i);
            return new Period("MONTH", key);
        }

        static Period year(Instant i) {
            String key = String.valueOf(
                    i.atZone(ZoneId.of("UTC")).getYear());
            return new Period("YEAR", key);
        }
    }
}

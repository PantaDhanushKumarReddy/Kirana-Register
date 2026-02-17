package com.example.Kirana.service;

import com.example.Kirana.dao.mongo.FinancialReportDao;
import com.example.Kirana.dto.event.TransactionEvent;
import com.example.Kirana.entity.mongo.FinancialReport;
import com.example.Kirana.enums.ReportPeriodType;
import org.springframework.stereotype.Service;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Service
public class ReportAggregationService {

    private final FinancialReportDao dao;

    public ReportAggregationService(FinancialReportDao dao) {
        this.dao = dao;
    }

    public void aggregate(TransactionEvent event) {

        Date createdAt = event.getCreatedAt();

        List<Period> periods = List.of(
                Period.week(createdAt),
                Period.month(createdAt),
                Period.year(createdAt)
        );

        for (Period p : periods) {

            FinancialReport report =
                    dao.findOrCreate(
                            event.getKiranaId(),
                            p.type,
                            p.key,
                            event.getCurrency()
                    );

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

            dao.save(report);
        }
    }

    private record Period(ReportPeriodType type, String key) {

        static Period week(Date d) {
            return new Period(
                    ReportPeriodType.WEEK,
                    new SimpleDateFormat("YYYY-'W'ww").format(d)
            );
        }

        static Period month(Date d) {
            return new Period(
                    ReportPeriodType.MONTH,
                    new SimpleDateFormat("MMM-yyyy").format(d)
            );
        }

        static Period year(Date d) {
            return new Period(
                    ReportPeriodType.YEAR,
                    new SimpleDateFormat("yyyy").format(d)
            );
        }
    }
}

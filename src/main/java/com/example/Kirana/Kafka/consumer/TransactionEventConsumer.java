package com.example.Kirana.Kafka.consumer;
import com.example.Kirana.dto.event.TransactionEvent;
import com.example.Kirana.service.ReportAggregationService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class TransactionEventConsumer {

    private final ReportAggregationService aggregationService;

    @Autowired
    public TransactionEventConsumer(ReportAggregationService aggregationService) {
        this.aggregationService = aggregationService;
    }

    @KafkaListener(
            topics = "transaction-events",
            groupId = "reporting-group"
    )
    public void consume(TransactionEvent event) {
        aggregationService.aggregate(event);
    }
}

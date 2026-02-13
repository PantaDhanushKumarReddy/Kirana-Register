package com.example.Kirana.Kafka.consumer;
import com.example.Kirana.dto.event.TransactionEvent;
import com.example.Kirana.service.ReportAggregationService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TransactionEventConsumer {

    private final ReportAggregationService aggregationService;

    @KafkaListener(
            topics = "transaction-events",
            groupId = "reporting-group"
    )
    public void consume(TransactionEvent event) {
        aggregationService.aggregate(event);
    }
}

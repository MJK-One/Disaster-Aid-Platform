package com.example.emergencyassistb4b4.report.kafka.producer;

import com.example.emergencyassistb4b4.global.kafka.dto.DisasterReportedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class DisasterReportedEventProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    private static final String TOPIC = "report-reported";

    // Kafka 메시지 발행 메서드
    public void sendDisasterReportedEvent(DisasterReportedEvent event) {

        kafkaTemplate.send(TOPIC, event)
                .thenAccept(result -> log.info("kafka - report-reported 발행 성공: {}", event))
                .exceptionally(ex -> {
                    log.error("kafka - report-reported 발행 실패: {}", event, ex);
                    return null;
                });
    }
}

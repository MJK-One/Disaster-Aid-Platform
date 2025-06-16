package com.example.emergencyassistb4b4.global.kafka.producer;

import com.example.emergencyassistb4b4.global.kafka.dto.DisasterAlertMessage;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class DisasterAlertProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    private static final String TOPIC = "disaster-alert";

    // Kafka 메시지 발행 메서드
    public void sendDisasterAlert(DisasterAlertMessage message) {

        kafkaTemplate.send(TOPIC, message)
                .thenAccept(result -> log.info("kafka - disaster-alert 발행 성공: {}", message))
                .exceptionally(ex -> {
                    log.error("kafka - disaster-alert 발행 실패: {}", message, ex);
                    return null;
                });
    }
}

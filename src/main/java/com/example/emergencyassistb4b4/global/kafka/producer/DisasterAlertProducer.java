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

    private final KafkaTemplate<String, String> kafkaTemplate;

    private final ObjectMapper objectMapper;

    private static final String TOPIC = "disaster-alert";

    // Kafka 메시지 발행 메서드
    public void sendDisasterAlert(DisasterAlertMessage message) {

        try {
            // 객체 -> JSON 문자열로 변환
            String msg = objectMapper.writeValueAsString(message);

            // Kafka 토픽에 메시지 발행
            kafkaTemplate.send(TOPIC, msg);
            log.info("kafka - disaster-alert 발행: {}", msg);

        } catch (JsonProcessingException e) {
            log.error("kafka 발행 실패", e);
        }
    }
}

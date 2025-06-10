package com.example.emergencyassistb4b4.global.kafka.consumer;

import com.example.emergencyassistb4b4.global.kafka.dto.DisasterAlertMessage;
import com.example.emergencyassistb4b4.alert.fcm.FcmService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class DisasterAlertConsumer {

    private final ObjectMapper objectMapper;

    private final FcmService fcmService; // FCM 발송 서비스

    // Kafka Listener -> Kafka 메시지를 수신
    @KafkaListener(topics = "disaster-alert", groupId = "disaster-alert-consumer-group")
    public void consumeDisasterAlert(String message) {

        try {
            // JSON 문자열 -> DisasterAlertMessage 객체로 역직렬화
            DisasterAlertMessage alertMessage = objectMapper.readValue(message, DisasterAlertMessage.class);

            log.info("kafka - disaster-alert 수신: {}", message);

            // FCM 발송
            fcmService.sendAlert(alertMessage);

        } catch (Exception e) {

            log.error("kafka - disaster-alert 처리 실패", e);

            throw new RuntimeException(e); // DLQ 적용 시 필요
        }
    }
}

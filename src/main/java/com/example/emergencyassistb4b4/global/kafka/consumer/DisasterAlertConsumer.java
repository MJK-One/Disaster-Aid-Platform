package com.example.emergencyassistb4b4.global.kafka.consumer;

import com.example.emergencyassistb4b4.alert.fcm.FcmFailureService;
import com.example.emergencyassistb4b4.alert.service.report.ReportImmediateAlertOrchestratorService;
import com.example.emergencyassistb4b4.global.kafka.dto.DisasterAlertMessage;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class DisasterAlertConsumer {

    private final ReportImmediateAlertOrchestratorService reportImmediateAlertOrchestratorService;

    // Kafka Listener -> Kafka 메시지를 수신
    @KafkaListener(
            topics = "disaster-alert",
            groupId = "disaster-alert-consumer-group",
            containerFactory = "kafkaListenerContainerFactory")
    public void consumeDisasterAlert(DisasterAlertMessage alertMessage) {

        try {
            log.info("kafka - disaster-alert 수신: {}", alertMessage);

            // FCM 발송
            reportImmediateAlertOrchestratorService.process(alertMessage);

        } catch (Exception e) {

            log.error("kafka - disaster-alert 처리 실패", e);

            throw new RuntimeException(e); // DLQ 적용 시 필요
        }
    }
}

package com.example.emergencyassistb4b4.alert.kafka.consumer.dlq;

import com.example.emergencyassistb4b4.alert.kafka.service.KafkaDlqLogService;
import com.example.emergencyassistb4b4.global.kafka.dto.DisasterReportedEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@RequiredArgsConstructor
@Component
public class ThresholdAlertDlqHandler { // Kafka DLT로 전송된 실패 메시지를 후처리(log 기록)하기 위한 리스너

    private final ObjectMapper objectMapper;
    private final KafkaDlqLogService kafkaDlqLogService;

    /**
     * 즉시 알림과 마찬가지로 동일한 DLQ 토픽(report-reported-dlt)을 구독하되,
     * 이 리스너는 임계값 기반 알림 리스너(alert-threshold-group)에서 실패한 메시지를 처리
     */
    @KafkaListener(
        topics = "report-reported-dlt",
        containerFactory = "disasterReportedDltListenerFactory"
    )
    public void handle(String rawMessage) {
        final String listener = "ImmediateAlertEventListener#onDisasterReported";
        LocalDateTime now = LocalDateTime.now();

        // 1) DLQ에 온 모든 메시지 기록
        kafkaDlqLogService.logFailure(
                "report-reported",            // 원래 토픽명
                "alert-threshold-group",      // consumer group
                rawMessage,                   // 원본 메시지
                "DLQ 도달(비즈니스 또는 역직렬화 실패)", // 요약 사유
                listener,                     // 리스너 식별자
                "",                           // 상세 예외(optional)
                now                           // 기록 시각
        );

        // 2) 역직렬화 및 부가 처리
        try {
            DisasterReportedEvent event = objectMapper.readValue(rawMessage, DisasterReportedEvent.class);
            log.warn("[DLQ] 역직렬화 성공, 이벤트: {}", event);
            // (필요 시 추가 후처리)
        } catch (Exception e) {
            log.error("[DLQ] 역직렬화 실패 - 이유: {}", e.getMessage(), e);
        }
    }
}
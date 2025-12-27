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
public class ImmediateAlertDlqHandler { // Kafka DLT로 전송된 실패 메시지를 후처리(log 기록)하기 위한 리스너

    private final ObjectMapper objectMapper; // DLQ로부터 받은 JSON 메시지를 객체로 변환
    private final KafkaDlqLogService kafkaDlqLogService; // DLQ 로깅 서비스 (DB 또는 파일 기록 등)

    /**
     * KafkaListener로 report-reported-dlt 토픽을 구독하고, 해당 토픽은 즉시 알림 리스너에서 처리 실패한 메시지가 전송됨
     * disasterReportedDltListenerFactory를 사용하여 String 타입 메시지 처리
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
                "alert-immediate-group",      // consumer group
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


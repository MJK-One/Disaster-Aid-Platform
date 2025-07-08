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
public class VolunteerUpdateAlertDlqHandler {

    private final ObjectMapper objectMapper;
    private final KafkaDlqLogService kafkaDlqLogService;

    @KafkaListener(
        topics = "volunteer-post-updated-dlt",
        containerFactory = "volunteerUpdatedDltListenerFactory"
    )
    public void handle(String rawMessage) {
        final String listener = "ImmediateAlertEventListener#onDisasterReported";
        LocalDateTime now = LocalDateTime.now();

        // 1) DLQ에 온 모든 메시지 기록
        kafkaDlqLogService.logFailure(
                "volunteer-post-updated",            // 원래 토픽명
                "alert-volunteer-update-group",      // consumer group
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

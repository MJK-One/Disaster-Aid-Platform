package com.example.emergencyassistb4b4.alert.kafka.consumer.dlq;

import com.example.emergencyassistb4b4.alert.kafka.service.KafkaDlqLogService;
import com.example.emergencyassistb4b4.global.kafka.dto.DisasterReportedEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class ImmediateAlertDlqHandler {

    private final ObjectMapper objectMapper;
    private final KafkaDlqLogService kafkaDlqLogService;

    @KafkaListener(
        topics = "report-reported-dlt",
        containerFactory = "disasterReportedDltListenerFactory"
    )
    public void handle(String rawMessage) {
        final String listener = "ImmediateAlertEventListener#onDisasterReported";

        DisasterReportedEvent parsedEvent = null;
        try {
            parsedEvent = objectMapper.readValue(rawMessage, DisasterReportedEvent.class);
        } catch (Exception e) {
            log.error("[DLQ:즉시알림] 역직렬화 실패 - 리스너: {}, 이유: {}", listener, e.getMessage());

            kafkaDlqLogService.logFailure(
                "report-reported",
                "alert-immediate-group",
                rawMessage,
                "역직렬화 실패로 인해 DLQ 메시지 파싱 불가",
                listener,
                e.getClass().getSimpleName() + ": " + e.getMessage(),
                LocalDateTime.now()
            );
            return;
        }

        log.warn("[DLQ:즉시알림] 역직렬화 성공 - 원인은 비즈니스 로직 처리 중 예외일 가능성 높음: {}", parsedEvent);
    }
}



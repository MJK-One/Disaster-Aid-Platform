package com.example.emergencyassistb4b4.global.kafka.consumer;

import ch.qos.logback.core.encoder.EchoEncoder;
import com.example.emergencyassistb4b4.alert.domain.AlertFailureLog;
import com.example.emergencyassistb4b4.alert.repository.AlertFailureLogRepository;
import com.example.emergencyassistb4b4.global.kafka.dto.DisasterAlertMessage;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class DisasterAlertDLQConsumer {

    private final ObjectMapper objectMapper;
    private final AlertFailureLogRepository alertFailureLogRepository;

    // Dead Letter Topic Listener -> 실패한 메시지 처리용
    @KafkaListener(topics = "disaster-alert.DLT", groupId = "disaster-alert-dlt-group")
    public void consumeDLT(String message) {

        try {
            DisasterAlertMessage alertMessage = objectMapper.readValue(message, DisasterAlertMessage.class);

            log.warn("[DLT] 알림 실패 수신 -> reportId={}", alertMessage.getReportId());

            // 실패 기록 저장
            AlertFailureLog failureLog = AlertFailureLog.builder()
                    .reportId(alertMessage.getReportId())
                    .alertMessage(message)
                    .failureReason("FCM 발송 3회 실패로 DLQ 전송됨")
                    .build();

            alertFailureLogRepository.save(failureLog);

            // (옵션) 관리자에게 알림 전송 (ex. 이메일, 슬랙, FCM 등)
            sendAdminNotification(alertMessage);

        } catch (Exception e) {

            log.error("[DLT] 처리 중 추가 오류 발생: {}", e);
        }
    }

    private  void sendAdminNotification(DisasterAlertMessage alertMessage) {

        // 예시: 슬랙 알림 or 이메일 발송 로직
        log.warn("[Admin 알림] FCM 실패 -> 관리자 확인 필요: {}", alertMessage);
    }
}

package com.example.emergencyassistb4b4.alert.kafka.consumer.listener;

import com.example.emergencyassistb4b4.alert.service.trigger.ReportThresholdAlertTriggerService;
import com.example.emergencyassistb4b4.global.kafka.dto.DisasterReportedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class ThresholdAlertEventListener {

    private final ReportThresholdAlertTriggerService triggerService;

    @KafkaListener(
        topics = "report-reported",
        containerFactory = "thresholdListenerFactory"
    )
    public void onDisasterReported(DisasterReportedEvent event) {
        try {
            triggerService.checkReportThreshold(event);
        } catch (Exception e) {
            log.error("[누적 알림 처리 실패] si={}, gu={}, type={}, time={}",
                event.getSi(), event.getGu(), event.getDisasterType(), event.getReportedAt(), e);
            throw e;
        }
    }
}

package com.example.emergencyassistb4b4.alert.kafka.service;

import com.example.emergencyassistb4b4.alert.kafka.domain.KafkaDlqLog;
import com.example.emergencyassistb4b4.alert.kafka.repository.KafkaDlqLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaDlqLogService {

    private final KafkaDlqLogRepository kafkaDlqLogRepository;

    public void logFailure(String topic, String consumerGroup, String payload, String reason, String listener, String exception, LocalDateTime failedAt) {
        try {
            KafkaDlqLog log = KafkaDlqLog.builder()
                .topic(topic)
                .consumerGroup(consumerGroup)
                .payload(payload)
                .reason(reason)
                .listener(listener)
                .exception(exception)
                .failedAt(failedAt)
                .build();

            kafkaDlqLogRepository.save(log);
        } catch (Exception e) {
            log.error("[Kafka DLQ] 로그 저장 실패! reason: {}", reason, e);
        }
    }
}

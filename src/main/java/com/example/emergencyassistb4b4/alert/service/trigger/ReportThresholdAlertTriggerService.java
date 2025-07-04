package com.example.emergencyassistb4b4.alert.service.trigger;

import com.example.emergencyassistb4b4.alert.orchestrator.ReportThresholdAlertOrchestratorService;
import com.example.emergencyassistb4b4.global.kafka.dto.DisasterReportedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReportThresholdAlertTriggerService {

    private final StringRedisTemplate redisTemplate;
    private final ReportThresholdAlertOrchestratorService reportAlertOrchestratorService;

    private static final Duration KEY_TTL = Duration.ofDays(1);
    private static final List<Long> THRESHOLDS = List.of(3L, 5L, 7L, 10L);

    public void checkReportThreshold(DisasterReportedEvent event) {
        String counterKey = generateReportCounterKey(event);

        try {
            Long count = redisTemplate.opsForValue().increment(counterKey);
            redisTemplate.expire(counterKey, KEY_TTL);
            log.info("누적 알림 카운트 - key={}, count={}", counterKey, count);

            if (isThreshold(count)) {
                String notifyKey = String.format("alert:%s:%d", counterKey, count);
                Boolean notified = redisTemplate.opsForValue()
                    .setIfAbsent(notifyKey, "true", KEY_TTL);

                if (Boolean.TRUE.equals(notified)) {
                    log.info("임계치 도달 - key={}, count={}", notifyKey, count);
                    reportAlertOrchestratorService.process(notifyKey);
                }
            }

        } catch (Exception e) {
            log.error("누적 알림 처리 실패 - redisKey={}", counterKey, e);
            throw e;
        }
    }

    // 재난 발생 지역(시, 도), 재난 타입, 날짜 기준으로 key 생성
    private String generateReportCounterKey(DisasterReportedEvent event) {
        return String.format("report:%s:%s:%s:%s",
            event.getProvince(), event.getCity(), event.getDisasterType(), event.getReportedAt().toLocalDate());
    }

    // 임계치 도달 여부 확인
    private boolean isThreshold(Long count) {
        return THRESHOLDS.contains(count);
    }
}

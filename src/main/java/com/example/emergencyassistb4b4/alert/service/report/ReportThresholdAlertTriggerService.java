package com.example.emergencyassistb4b4.alert.service.report;

import com.example.emergencyassistb4b4.global.kafka.dto.DisasterAlertMessage;
import java.time.Duration;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReportThresholdAlertTriggerService {

    private final StringRedisTemplate redisTemplate;
    private final ReportThresholdAlertOrchestratorService reportAlertOrchestratorService;

    private static final Duration KEY_TTL      = Duration.ofDays(1);
    private static final List<Long> THRESHOLDS = List.of(3L, 5L, 7L, 10L); // 임곗값 상의 후 결정

    public void checkReportThreshold(DisasterAlertMessage kafkaDto) {

        // 동일한 재난 신고 카운터 키 생성
        String reportCounterKey = generateReportCounterKey(kafkaDto);
        Long count = redisTemplate.opsForValue().increment(reportCounterKey);
        redisTemplate.expire(reportCounterKey, KEY_TTL);

        // 신고 건 수가 임계치에 도달했을 경우
        if (isThreshold(count)) {
            // 알림 발송 여부 확인용 notifyKey 생성
            String notifyKey = String.format("alert:%s:%d", reportCounterKey, count);
            Boolean notified = redisTemplate.opsForValue()
                .setIfAbsent(notifyKey, "true", KEY_TTL);

            // notifyKey 생성 되었을 경우
            if (Boolean.TRUE.equals(notified)) {
                // 알림 발송 준비
                reportAlertOrchestratorService.process(notifyKey);
            }
        }
    }

    // 재난 발생 지역(시, 도), 재난 타입, 날짜 기준으로 key 생성
    private String generateReportCounterKey(DisasterAlertMessage kafkaDto) {

        String[] location = kafkaDto.getLocation().split(" ");

        return String.format("report:%s:%s:%s:%s", location[0], location[1],
            kafkaDto.getDisasterType(), kafkaDto.getReportedAt().toLocalDate());
    }

    // 임계치 도달 여부 확인
    private boolean isThreshold(Long count) {
        return THRESHOLDS.contains(count);
    }

}

package com.example.emergencyassistb4b4.alert.fcm;

import com.example.emergencyassistb4b4.alert.domain.AlertFailureLog;
import com.example.emergencyassistb4b4.alert.repository.AlertFailureLogRepository;
import com.example.emergencyassistb4b4.global.exception.ApiException;
import com.example.emergencyassistb4b4.global.kafka.dto.DisasterAlertMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Service;

import static com.example.emergencyassistb4b4.global.status.ErrorStatus.ALERT_FAILED;

@Slf4j
@Service
@RequiredArgsConstructor
public class FcmFailureService {

    private final RetryTemplate retryTemplate; // 스프링 RetryTemplate 사용
    private final AlertFailureLogRepository alertFailureLogRepository;

    // 테스트 시 강제 실패 유도 플래그 (application-test.yml에서 설정 가능)
    @Value("${fcm.force-fail:false}")
    private boolean forceFail;

    // 알림 발송 메서드 (간단한 예시)
    public void sendAlert(DisasterAlertMessage alertMessage) {

        try {
            // 내부에서 정의한 블록이 실패할 경우 자동으로 지정된 횟수만큼 재시도함
            retryTemplate.execute(context -> {

                // 제목 및 메시지 구성
                String title = "[재난 알림]";
                String body = String.format("%s 에서 %s 발생", alertMessage.getLocation(), alertMessage.getDisasterType());

                log.info("FCM 발송 -> title: {}, body: {}", title, body);

                boolean success = simulateFcmSend(alertMessage);

                if (!success) throw new ApiException(ALERT_FAILED);

                // Retry 수행 대상 로직
                log.info("FCM 발송 시도 -> {}", alertMessage);

                return null;

            }, context -> { // 재시도 실패시 Recovery 콜백이 실행됨

                // 재시도 다 실패했을 경우에만 DB 저장
                log.error("FCM 발송 최종 실패: {}", alertMessage);

                alertFailureLogRepository.save(
                        AlertFailureLog.builder()
                                .reportId(alertMessage.getReportId())
                                .alertMessage(alertMessage.toString())
                                .failureReason("FCM 재시도 실패")
                                .build()
                );

                return null;
            });
        } catch (Exception e) {

            // Recovery 이후 추가 예외 발생 방지
            log.error("FCM 발송 중 예외 발생", e);
        }

        // 실제 FCM 서버 발송 부분 생략하고 로그로 대체 (지금은 예시로 log 출력, 운영에선 Firebase SDK 사용 ex. FirebaseMessaging.getInstance().send(message);)
    }

    // 실제 발송 로직 (운영 시 Firebase API 연동 필요)
    private boolean simulateFcmSend(DisasterAlertMessage alertMessage) {

        if (forceFail) return false;

        // 실제 FCM 발송 API 호출 로직 대신 무작위로 성공/실패 반환하는 테스트용 메서드
        return Math.random() > 0.3;
    }
}

package com.example.emergencyassistb4b4.attendance.consumer;

import com.example.emergencyassistb4b4.attendance.dto.MessageWrapper;
import com.example.emergencyassistb4b4.attendance.dto.SessionState;
import com.example.emergencyassistb4b4.attendance.dto.TrackingSessionDto;
import com.example.emergencyassistb4b4.attendance.service.TrackingDataService;
import com.example.emergencyassistb4b4.attendance.socket.handler.TrackingSocketHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class TrackingListener {
    private final TrackingSocketHandler trackingSocketHandler;
    private final TrackingDataService trackingDataService;

    @RabbitListener(queues = "tracking-delay-queue")
    public void handleTrackingMessage(MessageWrapper message) {
        if (message == null || message.getSessionState() == null || message.getPayload() == null) {
            log.warn("잘못된 메시지 수신: {}", message);
            return;
        }

        SessionState state = message.getSessionState();
        TrackingSessionDto dto = message.getPayload();
        Long teamId = dto.getTeamId();

        switch (state) {
            case READY -> {
                log.info("[READY] 세션 시작 알림 도착 - teamId={}, 시작 시각={}", teamId, dto.getStartTime());
                trackingSocketHandler.sendToTeam(teamId, "tracking_ready", dto);
            }

            case STARTED -> {
                log.info("[STARTED] 위치 요청 - teamId={}", teamId);
                trackingSocketHandler.sendToTeam(teamId, "location_request", dto);
            }

            case ENDED -> {
                log.info("[ENDED] 세션 종료 알림 도착 - teamId={}", teamId);

                trackingSocketHandler.sendToTeam(teamId, "tracking_ended", dto);
                // 1. 봉사자 목록 조회
                List<Long> volunteerIds=dto.getParticipantUserIds();


                // 2. 출석 및 위치 데이터 저장
                trackingDataService.saveSessionAttendanceData(volunteerIds, teamId);
            }

            default -> {
                log.warn("알 수 없는 세션 상태 수신: {}", state);
            }
        }
    }

}

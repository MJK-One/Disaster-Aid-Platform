package com.example.emergencyassistb4b4.attendance.socket.controller;

import com.example.emergencyassistb4b4.attendance.socket.dto.LocationUpdateMessage;
import com.example.emergencyassistb4b4.attendance.socket.notifier.TrackingNotifier;
import com.example.emergencyassistb4b4.attendance.socket.service.LocationWebSocketService;
import com.example.emergencyassistb4b4.location.service.LocationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;


@Controller
@RequiredArgsConstructor
@Slf4j
public class LocationWebSocketController {

    private final LocationService locationService;
    private final LocationWebSocketService locationWebSocketService;// 출석 체크 로직 담당
    private final TrackingNotifier trackingNotifier; // 실시간 알림 담당

    /**
     * 클라이언트로부터 위치 업데이트 메시지 수신
     */

    @MessageMapping("/location.update")
    public void handleLocationUpdate(LocationUpdateMessage message,
                                     @Header("Authorization") String token) {
        Long volunteerId = message.getVolunteerId();
        double lat = message.getLatitude();
        double lon = message.getLongitude();

        log.info("Received location update from volunteerId={} lat={} lon={}", volunteerId, lat, lon);

        // 1. Redis GEO 저장
        locationService.saveCoordinates(volunteerId, lat, lon);

        // 2. 출석 체크 수행 (해당 봉사자의 현재 팀과 정책 기준으로 체크)
        boolean isPresent = locationWebSocketService.checkAttendanceForVolunteer(volunteerId, lat, lon);

        // 3. 출석 상태 웹소켓으로 알림 전송
        trackingNotifier.notifyTrackingCheck(volunteerId, isPresent);

        //redis 에 저장
        locationWebSocketService.saveAndPublishAttendance(volunteerId,isPresent);

    }
}

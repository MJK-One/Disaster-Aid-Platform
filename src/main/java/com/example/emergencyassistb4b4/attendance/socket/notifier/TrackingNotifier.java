package com.example.emergencyassistb4b4.attendance.socket.notifier;

import com.example.emergencyassistb4b4.attendance.socket.handler.TrackingSocketHandler;
import com.example.emergencyassistb4b4.attendance.socket.message.TrackingMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TrackingNotifier {

    private final TrackingSocketHandler socketHandler;

    //출석 확인(프론트)
    public void notifyTrackingCheck(Long volunteerId, boolean present) {
        String status = present ? "present" : "absent";
        TrackingMessage message = new TrackingMessage("tracking_check", status);
        socketHandler.sendTrackingStatus(volunteerId, message);
    }

}

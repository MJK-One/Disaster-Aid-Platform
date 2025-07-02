package com.example.emergencyassistb4b4.alert.orchestrator;

import com.example.emergencyassistb4b4.alert.dto.report.ReportImmediateAlertDto;
import com.example.emergencyassistb4b4.alert.dto.fcm.FcmMessageDto;
import com.example.emergencyassistb4b4.alert.fcm.sender.FcmSender;
import com.example.emergencyassistb4b4.global.kafka.dto.DisasterReportedEvent;
import com.example.emergencyassistb4b4.user.domain.User;
import com.example.emergencyassistb4b4.user.service.UserService;
import com.example.emergencyassistb4b4.userDevice.domain.UserDevice;
import com.example.emergencyassistb4b4.userDevice.service.UserDeviceService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReportImmediateAlertOrchestratorService {

    private final FcmSender fcmSender;
    private final UserService userService;
    private final UserDeviceService userDeviceService;

    public void process(DisasterReportedEvent event) {
        ReportImmediateAlertDto info = ReportImmediateAlertDto.fromEvent(event);
        FcmMessageDto message = FcmMessageDto.fromReportImmediateAlert(info);

        User gov = userService.findGovernment(info.getSi());
        UserDevice device = userDeviceService.findByUserId(gov.getId());

        String token = device.getFcmToken();

        log.info("즉시 알림 발송 대상 - userId={}, token={}", gov.getId(), token);

        try {
            log.info("here");
            fcmSender.sendAlert(message, List.of(token));
        } catch (Exception e) {
            log.error("즉시 알림 발송 실패 - userId={}, token={}", gov.getId(), token, e);
            throw e;
        }
    }
}

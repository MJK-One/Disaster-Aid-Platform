package com.example.emergencyassistb4b4.alert.orchestrator;

import com.example.emergencyassistb4b4.alert.dto.fcm.FcmMessageDto;
import com.example.emergencyassistb4b4.alert.dto.report.ReportThresholdAlertDto;
import com.example.emergencyassistb4b4.alert.fcm.sender.FcmSender;
import com.example.emergencyassistb4b4.alert.service.command.AlertCommandService;
import com.example.emergencyassistb4b4.user.domain.User;
import com.example.emergencyassistb4b4.userDevice.domain.UserDevice;
import com.example.emergencyassistb4b4.userDevice.service.UserDeviceService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class ReportThresholdAlertOrchestratorService {

    private final AlertCommandService alertCommandService;
    private final UserDeviceService userDeviceService;
    private final FcmSender fcmSender;

    public void process(String notifyKey) {

        ReportThresholdAlertDto info = ReportThresholdAlertDto.fromKey(notifyKey);
        FcmMessageDto message = FcmMessageDto.fromReportThresholdAlert(info);

        List<UserDevice> devices = userDeviceService.findByRegion(info.getSi(), info.getGu());
        List<String> tokens = devices.stream().map(UserDevice::getFcmToken).toList();
        List<User> users = devices.stream().map(UserDevice::getUser).distinct().toList();

        log.info("누적 알림 발송 대상 {}명 - region={}, notifyKey={}",
            tokens.size(), info.getSi() + " " + info.getGu(), notifyKey);

        try {
            fcmSender.sendAlert(message, tokens);
            alertCommandService.saveReportAlert(info, users);
            log.info("누적 알림 DB 저장 완료 - notifyKey={}", notifyKey);
        } catch (Exception e) {
            log.error("누적 알림 발송 또는 저장 실패 - notifyKey={}", notifyKey, e);
            throw e;
        }
    }
}


package com.example.emergencyassistb4b4.alert.service.report;

import com.example.emergencyassistb4b4.alert.fcm.dto.FcmMessageDto;
import com.example.emergencyassistb4b4.alert.dto.report.ReportThresholdAlertDto;
import com.example.emergencyassistb4b4.alert.fcm.service.FcmSender;
import com.example.emergencyassistb4b4.alert.service.command.AlertCommandService;
import com.example.emergencyassistb4b4.user.domain.User;
import com.example.emergencyassistb4b4.userDevice.domain.UserDevice;
import com.example.emergencyassistb4b4.userDevice.service.UserDeviceService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class ReportThresholdAlertOrchestratorService {

    private final AlertCommandService alertCommandService;
    private final UserDeviceService userDeviceService;
    private final FcmSender fcmSender;

    public void process(String notifyKey) {

        // 1. notifyKey → ReportAlertDto
        ReportThresholdAlertDto info = ReportThresholdAlertDto.fromKey(notifyKey);

        // 2. FCM 메시지 생성
        FcmMessageDto message = FcmMessageDto.fromReportThresholdAlert(info);

        // 3. FCM 발송 대상 조회
        List<UserDevice> devices = userDeviceService.findByRegion(info.getSi(), info.getGu());
        List<String> tokens = devices.stream().map(UserDevice::getFcmToken).toList();
        List<User> users = devices.stream().map(UserDevice::getUser).distinct().toList();

        // 4. FCM 발송
        fcmSender.sendAlert(message, tokens);

        // 5. DB에 저장
        alertCommandService.saveReportAlert(info, users);
    }
}

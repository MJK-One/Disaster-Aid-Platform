package com.example.emergencyassistb4b4.alert.service.report;

import com.example.emergencyassistb4b4.alert.dto.report.ReportImmediateAlertDto;
import com.example.emergencyassistb4b4.alert.fcm.dto.FcmMessageDto;
import com.example.emergencyassistb4b4.alert.fcm.service.FcmSender;
import com.example.emergencyassistb4b4.report.domain.Report;
import com.example.emergencyassistb4b4.userDevice.service.UserDeviceService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReportImmediateAlertOrchestratorService {

    private final FcmSender fcmSender;
//    private final UserService userService;
    private final UserDeviceService userDeviceService;

    @Transactional(readOnly = true)
    public void process(Report report) {

        // 1. Report -> ReportReceivedAlertDto
        ReportImmediateAlertDto info = ReportImmediateAlertDto.fromReport(report);

        // 2. FCM 메시지 생성
        FcmMessageDto message = FcmMessageDto.fromReportImmediateAlert(info);

        // 3. FCM 발송 대상 조회
//        User government = userService.findGovernmentByNickname(info.getSi());
//        UserDevice device = userDeviceService.findByUserId(government.getId());
//        String token = device.getFcmToken();
        String token = ""; // 임시

        // 4. FCM 발송
        fcmSender.sendAlert(message, List.of(token));
    }
}



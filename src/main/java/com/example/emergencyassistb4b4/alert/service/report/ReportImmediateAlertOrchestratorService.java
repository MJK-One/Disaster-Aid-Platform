package com.example.emergencyassistb4b4.alert.service.report;

import com.example.emergencyassistb4b4.alert.dto.report.ReportImmediateAlertDto;
import com.example.emergencyassistb4b4.alert.fcm.dto.FcmMessageDto;
import com.example.emergencyassistb4b4.alert.fcm.service.FcmSender;
import com.example.emergencyassistb4b4.global.kafka.dto.DisasterAlertMessage;
import com.example.emergencyassistb4b4.user.domain.User;
import com.example.emergencyassistb4b4.user.service.UserService;
import com.example.emergencyassistb4b4.userDevice.domain.UserDevice;
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
    private final UserService userService;
    private final UserDeviceService userDeviceService;

    @Transactional(readOnly = true)
    public void process(DisasterAlertMessage kafkaDto) {

        // 1. DisasterAlertMessage(Kafka) -> ReportReceivedAlertDto
        ReportImmediateAlertDto info = ReportImmediateAlertDto.fromReport(kafkaDto);

        // 2. FCM 메시지 생성
        FcmMessageDto message = FcmMessageDto.fromReportImmediateAlert(info);

        // 3. FCM 발송 대상 조회
//        User government = userService.findGovernment(info.getSi());
//        UserDevice device = userDeviceService.findByUserId(government.getId());
//        String token = device.getFcmToken();

        // 4. FCM 발송
//        fcmSender.sendAlert(message, List.of(token));
    }
}



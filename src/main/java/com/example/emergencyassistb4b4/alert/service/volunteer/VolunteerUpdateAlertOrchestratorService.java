package com.example.emergencyassistb4b4.alert.service.volunteer;

import com.example.emergencyassistb4b4.alert.dto.volunteer.VolunteerUpdateAlertDto;
import com.example.emergencyassistb4b4.alert.fcm.dto.FcmMessageDto;
import com.example.emergencyassistb4b4.alert.fcm.service.FcmSender;
import com.example.emergencyassistb4b4.alert.service.command.AlertCommandService;
import com.example.emergencyassistb4b4.user.domain.User;
import com.example.emergencyassistb4b4.userDevice.domain.UserDevice;
import com.example.emergencyassistb4b4.userDevice.service.UserDeviceService;
import com.example.emergencyassistb4b4.volunteer.domain.Post;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class VolunteerUpdateAlertOrchestratorService {

    private final AlertCommandService alertCommandService;
    private final UserDeviceService userDeviceService;
    private final FcmSender fcmSender;

    @Transactional
    public void process(Post post) {

        // 1. Post -> VolunteerAlertDto
        VolunteerUpdateAlertDto info = VolunteerUpdateAlertDto.fromPost(post);

        // 2. FCM 메시지 생성
        FcmMessageDto message = FcmMessageDto.fromVolunteerUpdateAlert(info);

        // 3. FCM 발송 대상 조회
        // TODO: 봉사활동 참여자 + 토큰 조회
//        List<UserDevice> devices = userDeviceService.findByUserIds(users);
//        List<String> tokens = devices.stream().map(UserDevice::getFcmToken).toList();

        // 4. FCM 발송
//        fcmSender.sendAlert(message, tokens);

        // 5. DB에 저장
//        alertCommandService.saveVolunteerAlert(info, users);
    }

}

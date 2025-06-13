package com.example.emergencyassistb4b4.alert.fcm.service;

import com.example.emergencyassistb4b4.alert.fcm.dto.FcmMessageDto;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.MulticastMessage;
import com.google.firebase.messaging.Notification;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class FcmSender {

    private final FirebaseMessaging fcm;

    public void sendAlert(FcmMessageDto messageDto, List<String> tokens) {

        MulticastMessage message = MulticastMessage.builder()
            .addAllTokens(tokens)
            .setNotification(Notification.builder()
                .setTitle(messageDto.getTitle())
                .setBody(messageDto.getBody())
                .build())
            .build();

        try {
            fcm.sendEachForMulticast(message); // TODO: 발송 메서드 고민해보기
        } catch (Exception e) {
            log.error("FCM 메시지 전송 실패", e);
        }
    }
}

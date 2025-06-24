package com.example.emergencyassistb4b4.userDevice.service;

import static com.example.emergencyassistb4b4.global.status.ErrorStatus.CUSTOM_ERROR_STATUS;

import com.example.emergencyassistb4b4.global.exception.ApiException;
import com.example.emergencyassistb4b4.global.status.ErrorStatus;
import com.example.emergencyassistb4b4.user.domain.User;
import com.example.emergencyassistb4b4.user.repository.UserRepository;
import com.example.emergencyassistb4b4.userDevice.domain.UserDevice;
import com.example.emergencyassistb4b4.userDevice.dto.UserDeviceRequestDto;
import com.example.emergencyassistb4b4.userDevice.enums.DeviceOs;
import com.example.emergencyassistb4b4.userDevice.enums.DeviceType;
import com.example.emergencyassistb4b4.userDevice.repository.UserDeviceRepository;
import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserDeviceService {

    private final StringRedisTemplate redisTemplate;
    private final UserDeviceRepository userDeviceRepository;

    @Value("${fcm.test-token}")
    private String testFcmToken;

    // TODO : 프론트 연동 후 기기 등록/갱신 로직 구현 + 앱 설치/로그인 시 실행
    public void saveDevice(User user, UserDeviceRequestDto dto) {

        UserDevice device = userDeviceRepository
            .findByUser(user)
            .orElseGet(() -> UserDevice.builder() // (임시) 한 유저 당 하나의 기기만 등록
                .user(user)
                .type(DeviceType.from(dto.getType()))
                .os(DeviceOs.from(dto.getOs()))
                .osVersion(dto.getOsVersion())
                .model(dto.getModel())
                .fcmToken(dto.getFcmToken())
                .build());

        // 이미 있는 경우엔 토큰만 갱신
        if (device.getId() != null) {
            device.updateToken(dto.getFcmToken());
        }
        userDeviceRepository.save(device);
    }

    public void testSaveDevice(User user) {

        UserDevice device = userDeviceRepository
            .findByUser(user)
            .orElseGet(() -> UserDevice.builder() // (임시) 한 유저 당 하나의 기기만 등록
                .user(user)
                .fcmToken(testFcmToken)
                .build());

        // 이미 있는 경우엔 토큰만 갱신
        if (device.getId() != null) {
            device.updateToken(testFcmToken);
        }
        userDeviceRepository.save(device);

    }

    // TODO : 프론트 연동 후 Location Redis -> 특정 지역 디바이스 목록 조회
    public List<UserDevice> findByRegion(String si, String gu) {

//        String regionKey = String.format("region:%s:%s", si, gu);
//
//        Set<String> userIdStrings = redisTemplate.opsForSet().members(regionKey);
//
//        if (userIdStrings == null || userIdStrings.isEmpty()) {
//            return List.of();
//        }
//
//        List<Long> userIds = userIdStrings.stream()
//            .map(Long::parseLong)
//            .toList();

        // (임시) 전체 조회로 대체
        return userDeviceRepository.findAll();
    }

    public UserDevice findByUserId(Long userId) {
        return userDeviceRepository.findByUserId(userId)
                .orElseThrow(() -> new ApiException(ErrorStatus.USER_DEVICE_NOT_FOUND)); // TODO:에러코드설정
    }

    public List<UserDevice> findByUserIds(List<Long> userIds) {
        return userDeviceRepository.findByUserIdIn(userIds);
    }

}

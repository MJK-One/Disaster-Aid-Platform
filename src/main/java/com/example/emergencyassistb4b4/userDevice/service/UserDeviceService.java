package com.example.emergencyassistb4b4.userDevice.service;

import static com.example.emergencyassistb4b4.global.status.ErrorStatus.CUSTOM_ERROR_STATUS;

import com.example.emergencyassistb4b4.global.exception.ApiException;
import com.example.emergencyassistb4b4.userDevice.domain.UserDevice;
import com.example.emergencyassistb4b4.userDevice.repository.UserDeviceRepository;
import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserDeviceService {

    private final StringRedisTemplate redisTemplate;
    private final UserDeviceRepository userDeviceRepository;

    private static final Duration REGION_KEY_TTL = Duration.ofMinutes(5);

    // TODO : 프론트 연동 시 기기 등록/갱신 로직 구현

    // Location Redis에서 특정 지역의 디바이스 목록 조회
    public List<UserDevice> findByRegion(String si, String gu) {

        String regionKey = String.format("region:%s:%s", si, gu);

        Set<String> deviceIds = redisTemplate.opsForSet().members(regionKey);

        if (deviceIds == null || deviceIds.isEmpty()) {
            return Collections.emptyList();
        }

        // 만료 연장: 계속 조회가 일어나면 TTL 갱신
        redisTemplate.expire(regionKey, REGION_KEY_TTL);

        return userDeviceRepository.findByDeviceIdIn(deviceIds);
    }

    public UserDevice findByUserId(Long userId) {
        return userDeviceRepository.findByUserId(userId)
            .orElseThrow(() -> new ApiException(CUSTOM_ERROR_STATUS)); // TODO:에러코드설정
    }

    public List<UserDevice> findByUserIds(List<Long> userIds) {
        return userDeviceRepository.findByUserIdIn(userIds);
    }

}

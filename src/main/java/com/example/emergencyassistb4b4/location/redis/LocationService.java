package com.example.emergencyassistb4b4.location.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class LocationService {

    private final RedisTemplate<String, Object> redisTemplate;
    // 주기적인 저장은 프론트에서 실시 예정

    // 행정 구역 저장
    public void saveRegion(Long userId, String si, String gu) {
        String regionKey = si + " " + gu;

        redisTemplate.opsForValue().set(regionKey, userId);
        redisTemplate.expire(regionKey, Duration.ofMinutes(5));
    }

    // 봉사자 위치 저장
    public void saveCoordinates(Long userId, double latitude, double longitude) {
        String key = "coordinates:" + userId;

        Map<String, Object> coordinates = new HashMap<>();
        coordinates.put("latitude", latitude);
        coordinates.put("longitude", longitude);

        redisTemplate.opsForHash().putAll(key, coordinates);
        // Optional: TTL 설정
        redisTemplate.expire(key, Duration.ofMinutes(1));
    }

    // 재난 알림 사용시(si + " " + gu 형태)
    public List<Object> getRegion(String region) {
        return redisTemplate.opsForList().range(region, 0, -1);
    }

    //봉사자 알림 사용시
    public Map<Object, Object> getCoordinates(String userId) {
        return redisTemplate.opsForHash().entries("coordinates:" + userId);
    }
}

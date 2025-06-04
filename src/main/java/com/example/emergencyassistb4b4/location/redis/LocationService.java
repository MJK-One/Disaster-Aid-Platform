package com.example.emergencyassistb4b4.location.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class LocationService {

    private final RedisTemplate<String, Object> redisTemplate;

    // 재난 시/구 저장
    public void saveRegion(String userId, String city, String district) {
        String region = city + " " + district; // 예: "서울시 강남구"
        redisTemplate.opsForValue().set("region:" + userId, region);
    }

    // 사용자 좌표 저장
    public void saveCoordinates(String userId, double latitude, double longitude) {
        Map<String, Double> coordinates = new HashMap<>();
        coordinates.put("latitude", latitude);
        coordinates.put("longitude", longitude);
        redisTemplate.opsForHash().putAll("coordinates:" + userId, coordinates);
    }
}


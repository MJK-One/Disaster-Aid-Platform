package com.example.emergencyassistb4b4.location.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.geo.*;
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class LocationService {

    // String RedisTemplate 명시적 주입 (RedisConfig에 stringRedisTemplate 빈으로 정의되어 있다고 가정)
    @Qualifier("stringRedisTemplate")
    private final RedisTemplate<String, String> redisTemplate;

    // 행정 구역 저장
    public void saveRegion(Long userId, String si, String gu) {
        String regionKey = si + " " + gu;
        redisTemplate.opsForValue().set(regionKey, userId.toString());
        redisTemplate.expire(regionKey, Duration.ofMinutes(5));
    }

    // 봉사자 위치 저장
    public void saveCoordinates(Long userId, double latitude, double longitude) {
        String key = "user:locations";

        // 좌표 순서: longitude, latitude 로 변경
        redisTemplate.opsForGeo().add(key, new Point(longitude, latitude), userId.toString());

        // TTL 별도 관리
        String expireKey = "location:ttl:" + userId;
        redisTemplate.opsForValue().set(expireKey, "1", Duration.ofMinutes(1));
    }

    // 재난 알림 사용시(si + " " + gu 형태)
    public List<String> getRegion(String region) {
        return redisTemplate.opsForList().range(region, 0, -1);
    }

    // 봉사자 좌표 조회
    public Map<String, Double> getCoordinates(String userId) {
        String key = "user:locations";

        List<Point> positions = redisTemplate.opsForGeo().position(key, userId);
        if (positions == null || positions.isEmpty() || positions.get(0) == null) {
            return Collections.emptyMap();
        }
        Point point = positions.get(0);

        Map<String, Double> coordinates = new HashMap<>();
        coordinates.put("latitude", point.getY());
        coordinates.put("longitude", point.getX());

        return coordinates;
    }

    // 반경 내 사용자 조회
    public List<String> findUsersWithinRadius(double latitude, double longitude, double radiusMeters) {
        String key = "user:locations";

        Point center = new Point(longitude, latitude);
        Distance distance = new Distance(radiusMeters / 1000.0, Metrics.KILOMETERS);

        GeoResults<RedisGeoCommands.GeoLocation<String>> results = redisTemplate.opsForGeo()
                .radius(key, String.valueOf(center), distance);

        if (results == null) {
            return Collections.emptyList();
        }

        return results.getContent().stream()
                .map(GeoResult::getContent)
                .map(RedisGeoCommands.GeoLocation::getName)
                .toList();
    }
}

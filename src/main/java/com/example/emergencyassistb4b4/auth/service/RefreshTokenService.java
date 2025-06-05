package com.example.emergencyassistb4b4.auth.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@RequiredArgsConstructor
@Service
public class RefreshTokenService { // Refresh 토큰을 저장/조회/삭제 ( RedisTemplate 을 사용 )

    private final RedisTemplate<String, Object> redisTemplate;
    private static final long REFRESH_TOKEN_EXPIRE = 60 * 60 * 24 * 7;

    public void saveToken(Long userId, String refreshToken) {
        redisTemplate.opsForValue().set(
                getKey(userId),
                refreshToken,
                Duration.ofSeconds(REFRESH_TOKEN_EXPIRE));
    }

    private String getKey(Long userId) {
        return "refresh_token:" + userId;
    }

    public String getRefreshToken(Long userId) {
        return (String) redisTemplate.opsForValue().get(getKey(userId));
    }

    public void deleteRefreshToken(Long userId) {
        redisTemplate.delete(getKey(userId));
    }
}

package com.example.emergencyassistb4b4.auth.token;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RedisService {
    private final StringRedisTemplate stringRedisTemplate;

    public void deleteRefreshToken(String email) {
        stringRedisTemplate.delete("refresh:" + email);
    }
    public void addToBlackList(String token, long ttlMillis) {
        stringRedisTemplate.opsForValue().set("blacklist:" + token, ttlMillis + "ms");
    }
    public boolean isBlacklisted(String token) {
        return stringRedisTemplate.hasKey("blacklist:" + token);
    }
}

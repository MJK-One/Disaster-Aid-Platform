package com.example.emergencyassistb4b4.auth.redis;

import org.springframework.data.annotation.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@RedisHash(value = "refresh_token", timeToLive = 1209600)
public class RefreshToken {
    @Id
    private Long userId;
    @Indexed
    private String refreshToken;

    public RefreshToken update(String newRefreshToken) {
        this.refreshToken = newRefreshToken;
        return this;
    }
}

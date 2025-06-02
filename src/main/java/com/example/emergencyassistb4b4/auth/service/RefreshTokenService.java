package com.example.emergencyassistb4b4.auth.service;

import com.example.emergencyassistb4b4.auth.redis.RefreshToken;
import com.example.emergencyassistb4b4.auth.redis.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class RefreshTokenService {
    private final RefreshTokenRepository refreshTokenRepository;

    public RefreshToken findByRefreshToken(String refreshToken) {
        return refreshTokenRepository.findByRefreshToken(refreshToken)
                .orElseThrow(() -> new IllegalArgumentException("해당 리프레시 토큰을 찾을 수 없습니다."));
    }
    public void save(RefreshToken refreshToken) {
        refreshTokenRepository.save(refreshToken);
    }
}

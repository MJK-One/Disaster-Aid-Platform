package com.example.emergencyassistb4b4.auth.strategy;

import com.example.emergencyassistb4b4.auth.dto.LoginResponse;
import com.example.emergencyassistb4b4.auth.jwt.JwtTokenProvider;
import com.example.emergencyassistb4b4.auth.redis.RefreshTokenService;
import com.example.emergencyassistb4b4.user.dto.UserResponse;
import com.example.emergencyassistb4b4.user.service.UserReadService;

import lombok.RequiredArgsConstructor;

import java.time.Duration;

@RequiredArgsConstructor
public abstract class AbstractLoginStrategy implements LoginStrategy {
    protected final UserReadService userReadService;
    protected final JwtTokenProvider jwtTokenProvider;
    protected final RefreshTokenService refreshTokenService;

    protected LoginResponse issueTokens(UserResponse userResponse) {
        String accessToken = jwtTokenProvider.generateToken(userResponse, Duration.ofHours(1));
        String refreshToken = jwtTokenProvider.generateToken(userResponse, Duration.ofDays(14));
        refreshTokenService.saveRefreshToken(userResponse.getId(), refreshToken);
        return LoginResponse.of(accessToken, refreshToken);
    }
}

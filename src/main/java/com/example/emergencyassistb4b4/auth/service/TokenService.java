package com.example.emergencyassistb4b4.auth.service;

import com.example.emergencyassistb4b4.auth.jwt.JwtTokenProvider;
import com.example.emergencyassistb4b4.auth.redis.RefreshTokenService;
import com.example.emergencyassistb4b4.global.exception.ApiException;
import com.example.emergencyassistb4b4.global.status.ErrorStatus;
import com.example.emergencyassistb4b4.user.dto.UserResponse;
import com.example.emergencyassistb4b4.user.service.UserReadService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.Duration;

@RequiredArgsConstructor
@Service
public class TokenService {
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenService refreshTokenService;
    private final UserReadService userReadService;

    public String createNewAccessToken(String refreshToken) {
        // 리프레시 토큰 유효성 검사
        if (!jwtTokenProvider.validateToken(refreshToken)) {
            throw new ApiException(ErrorStatus.INVAlID_REFRESH_TOKEN);
        }
        // Redis 에서 리프레시 토큰으로 userId 조회
        Long userId = refreshTokenService.findByRefreshToken(refreshToken).getUserId();

        // User 조회
        UserResponse user = userReadService.findById(userId);

        // 새로운 액세스 토큰 발급
        return jwtTokenProvider.generateToken(user, Duration.ofHours(2));
    }

}
// createNewAccessToken()메서드는 전달받은 리프레시 토큰으로
// 1. 토큰 유효성 검사를 진행하고
// 2. 유효한 토큰일 때 리프레시 토큰으로 사용자 ID를 찾는다
// 3. 사용자 ID로 사용자를 찾은 후에
// 토큰 제공자의 generateToken()메서드를 호출해 새로운 액세스 토큰을 생성
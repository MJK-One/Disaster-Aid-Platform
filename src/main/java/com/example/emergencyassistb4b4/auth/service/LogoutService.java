package com.example.emergencyassistb4b4.auth.service;

import com.example.emergencyassistb4b4.auth.token.RedisService;
import com.example.emergencyassistb4b4.global.exception.ApiException;
import com.example.emergencyassistb4b4.global.security.JwtUtils;
import com.example.emergencyassistb4b4.global.status.ErrorStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class LogoutService {
    private final JwtUtils jwtUtils;
    private final RedisService redisService;

    public void logout(String token) {
        // 1. token 유효성 검사
        if (!jwtUtils.validateToken(token)) {
            throw new ApiException(ErrorStatus.INVAlID_ACCESS_TOKEN);
        }
        // 2. 토큰 사용자 이메일 추출
        String email = jwtUtils.getEmailFromToken(token);

        // 3. RefreshToken Redis에서 제거
        redisService.deleteRefreshToken(email);

        // 4. AccessToken 블랙리스트 등록
        long expiration = jwtUtils.getRemainingExpiration(token);
        redisService.addToBlackList(token, expiration);
    }
}

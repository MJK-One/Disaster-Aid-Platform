package com.example.emergencyassistb4b4.auth.strategy;

import com.example.emergencyassistb4b4.auth.dto.LoginRequest;
import com.example.emergencyassistb4b4.auth.dto.LoginResponse;
import com.example.emergencyassistb4b4.auth.jwt.JwtTokenProvider;
import com.example.emergencyassistb4b4.auth.redis.RefreshTokenService;
import com.example.emergencyassistb4b4.user.domain.LoginType;
import com.example.emergencyassistb4b4.user.domain.UserRole;
import com.example.emergencyassistb4b4.user.dto.UserResponse;
import com.example.emergencyassistb4b4.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
@RequiredArgsConstructor
public class IndividualGoogleStrategy implements LoginStrategy {
    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;
    private  final RefreshTokenService refreshTokenService;
    @Override
    public boolean supports(UserRole userRole, LoginType loginType) {
        return userRole == UserRole.IND && loginType == LoginType.GOOGLE;
    }

    @Override
    public LoginResponse login(LoginRequest loginRequest) {
        UserResponse userResponse = userService.findByEmail(loginRequest.getEmail());
        String accessToken = jwtTokenProvider.generateToken(userResponse, Duration.ofHours(1));
        String refreshToken = jwtTokenProvider.generateToken(userResponse, Duration.ofDays(14));

        refreshTokenService.saveRefreshToken(refreshToken, userResponse.getId());
        return LoginResponse.of(accessToken, refreshToken);

    }
}

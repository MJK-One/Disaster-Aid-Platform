package com.example.emergencyassistb4b4.auth.strategy;

import com.example.emergencyassistb4b4.auth.dto.LoginRequest;
import com.example.emergencyassistb4b4.auth.dto.LoginResponse;
import com.example.emergencyassistb4b4.auth.jwt.JwtTokenProvider;
import com.example.emergencyassistb4b4.auth.redis.RefreshTokenService;
import com.example.emergencyassistb4b4.user.domain.LoginType;
import com.example.emergencyassistb4b4.user.domain.UserRole;
import com.example.emergencyassistb4b4.user.dto.UserResponse;
import com.example.emergencyassistb4b4.user.service.UserReadService;
import com.example.emergencyassistb4b4.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
public class IndividualLocalStrategy extends AbstractLoginStrategy {
    private final UserService userService;


    public IndividualLocalStrategy(UserReadService userReadService, JwtTokenProvider jwtTokenProvider, RefreshTokenService refreshTokenService, UserService userService) {
        super(userReadService, jwtTokenProvider, refreshTokenService);
        this.userService = userService;
    }
    @Override
    public boolean supports(UserRole userRole, LoginType loginType) {
        return userRole == UserRole.IND && loginType == LoginType.LOCAL;
    }

    @Override
    public LoginResponse login(LoginRequest loginRequest) {
        UserResponse userResponse = userService.validateUserCredentials(loginRequest.getEmail(), loginRequest.getPassword());
        return issueTokens(userResponse);

    }
}

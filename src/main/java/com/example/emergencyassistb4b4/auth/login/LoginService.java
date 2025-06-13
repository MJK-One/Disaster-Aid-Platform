package com.example.emergencyassistb4b4.auth.login;

import com.example.emergencyassistb4b4.auth.dto.LoginRequestDto;
import com.example.emergencyassistb4b4.auth.dto.TokenResponseDto;
import com.example.emergencyassistb4b4.auth.login.strategy.LoginStrategy;
import com.example.emergencyassistb4b4.auth.token.TokenService;
import com.example.emergencyassistb4b4.global.exception.ApiException;
import com.example.emergencyassistb4b4.global.status.ErrorStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LoginService {
    private final List<LoginStrategy> strategies;

    public TokenResponseDto login(LoginRequestDto loginRequestDto) {
        return strategies.stream()
                .filter(strategy -> strategy.supports(loginRequestDto.getUserRole(), loginRequestDto.getLoginType()))
                .findFirst()
                .orElseThrow(() -> new ApiException(ErrorStatus.LOGIN_STRATEGY_NOT_FOUND))
                .login(loginRequestDto);
    }
}

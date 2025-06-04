package com.example.emergencyassistb4b4.auth.service;

import com.example.emergencyassistb4b4.auth.dto.LoginRequest;
import com.example.emergencyassistb4b4.auth.dto.LoginResponse;
import com.example.emergencyassistb4b4.auth.strategy.LoginStrategy;
import com.example.emergencyassistb4b4.global.exception.ApiException;
import com.example.emergencyassistb4b4.global.status.ErrorStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class LoginService {
    private final List<LoginStrategy> loginStrategyList;

    public LoginResponse login(LoginRequest loginRequest) {
        return loginStrategyList.stream()
                .filter(strategy -> strategy.supports(loginRequest.getUserRole(), loginRequest.getLoginType()))
                .findFirst()
                .orElseThrow(()-> new ApiException(ErrorStatus.LOGIN_STRATEGY_NOT_FOUND))
                .login(loginRequest);
    }
}

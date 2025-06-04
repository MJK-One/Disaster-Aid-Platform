package com.example.emergencyassistb4b4.auth.strategy;


import com.example.emergencyassistb4b4.auth.dto.LoginRequest;
import com.example.emergencyassistb4b4.auth.dto.LoginResponse;
import com.example.emergencyassistb4b4.user.domain.LoginType;
import com.example.emergencyassistb4b4.user.domain.UserRole;

public interface LoginStrategy {
    boolean supports(UserRole userRole, LoginType loginType);
    LoginResponse login(LoginRequest loginRequest);
}

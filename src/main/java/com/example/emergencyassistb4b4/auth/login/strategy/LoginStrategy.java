package com.example.emergencyassistb4b4.auth.login.strategy;

import com.example.emergencyassistb4b4.auth.dto.LoginRequestDto;
import com.example.emergencyassistb4b4.auth.dto.TokenResponseDto;
import com.example.emergencyassistb4b4.user.domain.LoginType;
import com.example.emergencyassistb4b4.user.domain.UserRole;

public interface LoginStrategy {
    boolean supports(UserRole userRole, LoginType loginType);
    TokenResponseDto login(LoginRequestDto loginRequestDto);
}

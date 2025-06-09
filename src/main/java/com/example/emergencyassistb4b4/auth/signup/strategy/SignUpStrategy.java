package com.example.emergencyassistb4b4.auth.signup.strategy;

import com.example.emergencyassistb4b4.auth.dto.TokenResponseDto;
import com.example.emergencyassistb4b4.auth.signup.SignUpRequestDto;
import com.example.emergencyassistb4b4.user.domain.LoginType;
import com.example.emergencyassistb4b4.user.domain.UserRole;

public interface SignUpStrategy {
    boolean supports(UserRole userRole, LoginType loginType);
    TokenResponseDto signUp(SignUpRequestDto signUpRequest);
}

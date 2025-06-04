package com.example.emergencyassistb4b4.auth.dto;

import com.example.emergencyassistb4b4.user.domain.LoginType;
import com.example.emergencyassistb4b4.user.domain.UserRole;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class LoginRequest {
    private String email;
    private String password;
    private UserRole userRole;
    LoginType loginType;
}

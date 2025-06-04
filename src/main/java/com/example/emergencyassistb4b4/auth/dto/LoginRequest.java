package com.example.emergencyassistb4b4.auth.dto;

import com.example.emergencyassistb4b4.user.domain.LoginType;
import com.example.emergencyassistb4b4.user.domain.UserRole;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class LoginRequest {
    private final String email;
    private final String password;
    private final UserRole userRole;
    private final LoginType loginType;
}

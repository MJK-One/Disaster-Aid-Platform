package com.example.emergencyassistb4b4.auth.dto;

import com.example.emergencyassistb4b4.user.domain.LoginType;
import com.example.emergencyassistb4b4.user.domain.UserRole;

public record LoginResponseDto(
        String email,
        String password,
        UserRole userRole,
        LoginType loginType
) {
}

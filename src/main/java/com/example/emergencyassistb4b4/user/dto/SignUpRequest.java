package com.example.emergencyassistb4b4.user.dto;

import com.example.emergencyassistb4b4.user.domain.LoginType;
import com.example.emergencyassistb4b4.user.domain.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class SignUpRequest {
    private String email;
    private String password;
    private String nickname;
    private String phoneNumber;
    private UserRole userRole;
    private LoginType loginType;

    //소셜 전용
    private String provider;
    private String providerId;

    // 단체 전용
    private String organizationName;
}
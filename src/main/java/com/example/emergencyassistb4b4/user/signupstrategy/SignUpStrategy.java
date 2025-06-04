package com.example.emergencyassistb4b4.user.signupstrategy;

import com.example.emergencyassistb4b4.user.domain.LoginType;
import com.example.emergencyassistb4b4.user.domain.UserRole;
import com.example.emergencyassistb4b4.user.dto.SignUpRequest;

public interface SignUpStrategy {
    boolean supports(UserRole userRole, LoginType loginType);
    void signUp(SignUpRequest signUpRequest);
}

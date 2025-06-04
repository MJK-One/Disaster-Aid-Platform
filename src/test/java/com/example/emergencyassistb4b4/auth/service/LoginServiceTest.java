package com.example.emergencyassistb4b4.auth.service;

import com.example.emergencyassistb4b4.auth.dto.LoginRequest;
import com.example.emergencyassistb4b4.auth.dto.LoginResponse;
import com.example.emergencyassistb4b4.user.domain.LoginType;
import com.example.emergencyassistb4b4.user.domain.UserRole;
import com.example.emergencyassistb4b4.user.dto.SignUpRequest;
import com.example.emergencyassistb4b4.user.repository.UserRepository;
import com.example.emergencyassistb4b4.user.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
@Transactional
class LoginServiceTest {
    @Autowired
    private UserService userService;

    @Autowired
    private LoginService loginService;

    @Autowired
    private UserRepository userRepository;
    @Test
    @DisplayName("로그인 시 accessToken 과 refreshToken 을 발급받을 수 있다")
    void loginShouldIssueTokens() {
        // given
        String email = "loginuser@example.com";
        String rawPassword = "test1234";

        SignUpRequest request = SignUpRequest.builder()
                .email(email)
                .password(rawPassword)
                .nickname("tester")
                .phoneNumber("01012345678")
                .userRole(UserRole.IND)
                .loginType(LoginType.LOCAL)
                .build();
        userService.signUp(request);

        LoginRequest loginRequest = new LoginRequest(email, rawPassword, UserRole.IND, LoginType.LOCAL);

        // when
        LoginResponse response = loginService.login(loginRequest);

        // then
        assertThat(response.getAccessToken()).isNotBlank();
        assertThat(response.getRefreshToken()).isNotBlank();
    }
}
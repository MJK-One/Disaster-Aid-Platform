package com.example.emergencyassistb4b4.user.service;

import com.example.emergencyassistb4b4.user.domain.LoginType;
import com.example.emergencyassistb4b4.user.domain.UserRole;
import com.example.emergencyassistb4b4.user.dto.SignUpRequest;
import com.example.emergencyassistb4b4.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class UserServiceSignUpTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("IND + LOCAL 조합으로 회원가입하면 유저가 저장된다.")
    void signUpWithIndLocal_shouldSaveUser() {
        // given
        String email = "testuser@example.com";
        SignUpRequest request = SignUpRequest.builder()
                .email(email)
                .password("test1234")
                .nickname("tester")
                .phoneNumber("01012345678")
                .userRole(UserRole.IND)
                .loginType(LoginType.LOCAL)
                .build();

        // when
        userService.signUp(request);

        // then
        boolean exists = userRepository.existsByEmail(email);
        assertThat(exists).isTrue();
    }
}
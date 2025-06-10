package com.example.emergencyassistb4b4.auth.signup;

import com.example.emergencyassistb4b4.auth.dto.TokenResponseDto;
import com.example.emergencyassistb4b4.auth.signup.strategy.SignUpStrategy;
import com.example.emergencyassistb4b4.global.exception.ApiException;
import com.example.emergencyassistb4b4.global.status.ErrorStatus;
import com.example.emergencyassistb4b4.user.domain.LoginType;
import com.example.emergencyassistb4b4.user.domain.UserRole;
import com.example.emergencyassistb4b4.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@SpringBootTest
@Transactional
class SignUpServiceTest {
  @Autowired
    private SignUpService signUpService;
    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("일반 회원가입 성공 및 토큰 발급")
    void localSignupSuccess() throws Exception {
        //given
        SignUpRequestDto request = SignUpRequestDto.builder()
                .email("testuser@example.com")
                .password("test1234!")
                .name("testUser")
                .userRole(UserRole.IND)
                .loginType(LoginType.LOCAL)
                .build();
        //when
        TokenResponseDto result = signUpService.signup(request);

        //then
        assertThat(result).isNotNull();
        assertThat(result.accessToken()).isNotBlank();
        assertThat(result.refreshToken()).isNotBlank();
    }

    @Test
    @DisplayName("카카오 회원가입 성공 및 토큰 발급")
    void kakaoSignupSuccess() throws Exception {
        // given
        SignUpRequestDto request = SignUpRequestDto.builder()
                .email("kakao@example.com")
                .name("kakaoUser")
                .userRole(UserRole.IND)
                .loginType(LoginType.KAKAO)
                .build();
        //when
        TokenResponseDto result = signUpService.signup(request);
        //then
        assertThat(result).isNotNull();
        assertThat(result.accessToken()).isNotBlank();
        assertThat(result.refreshToken()).isNotBlank();
    }


}
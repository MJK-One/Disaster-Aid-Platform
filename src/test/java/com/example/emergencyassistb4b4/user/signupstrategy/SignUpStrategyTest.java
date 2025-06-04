package com.example.emergencyassistb4b4.user.signupstrategy;

import com.example.emergencyassistb4b4.user.domain.LoginType;
import com.example.emergencyassistb4b4.user.domain.User;
import com.example.emergencyassistb4b4.user.domain.UserRole;
import com.example.emergencyassistb4b4.user.dto.SignUpRequest;
import com.example.emergencyassistb4b4.user.repository.UserRepository;
import com.example.emergencyassistb4b4.user.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
@Transactional
@DisplayName("회원가입 전략 실행 테스트")
class SignUpStrategyTest {
    @Autowired
    @MockitoBean
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("IND + LOCAL 조합이면 IndividualLocalSignupStrategy가 동작한다.")
    void testStrategyExecutionForIndLocal() {
        // given
        SignUpRequest request = SignUpRequest.builder()
                .email("test@naver.com")
                .password("test1234")
                .nickname("tester")
                .phoneNumber("01012345678")
                .loginType(LoginType.LOCAL)
                .userRole(UserRole.IND)
                .build();

        // when

        userService.signUp(request);
        userRepository.flush();
        System.out.println("등록된 유저 있음? = " + userRepository.existsByEmail("test@naver.com"));

        // then
        boolean exists = userRepository.existsByEmail("test@naver.com");
        assertThat(exists).isTrue();

    }
}
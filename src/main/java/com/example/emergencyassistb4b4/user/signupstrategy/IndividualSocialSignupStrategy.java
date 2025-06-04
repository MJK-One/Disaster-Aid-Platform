package com.example.emergencyassistb4b4.user.signupstrategy;

import com.example.emergencyassistb4b4.user.domain.LoginType;
import com.example.emergencyassistb4b4.user.domain.User;
import com.example.emergencyassistb4b4.user.domain.UserRole;
import com.example.emergencyassistb4b4.user.dto.SignUpRequest;
import com.example.emergencyassistb4b4.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class IndividualSocialSignupStrategy implements SignUpStrategy {
    private final UserRepository userRepository;
    @Override
    public boolean supports(UserRole userRole, LoginType loginType) {
        return userRole == UserRole.IND && loginType != LoginType.LOCAL;
    }

    @Override
    public void signUp(SignUpRequest requestDto) {
        // 소셜 개인 회원가입 로직

        User user = User.builder()
                .email(requestDto.getEmail())
                .nickname(requestDto.getNickname())
                .loginType(LoginType.LOCAL)
                .userRole(UserRole.IND)
                .provider(requestDto.getProvider())
                .build();
        userRepository.save(user);
    }
}

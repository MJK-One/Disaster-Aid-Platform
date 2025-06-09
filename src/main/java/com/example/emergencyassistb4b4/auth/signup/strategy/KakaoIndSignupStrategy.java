package com.example.emergencyassistb4b4.auth.signup.strategy;

import com.example.emergencyassistb4b4.auth.dto.TokenResponseDto;
import com.example.emergencyassistb4b4.auth.signup.SignUpRequestDto;
import com.example.emergencyassistb4b4.auth.token.TokenService;
import com.example.emergencyassistb4b4.user.domain.LoginType;
import com.example.emergencyassistb4b4.user.domain.User;
import com.example.emergencyassistb4b4.user.domain.UserRole;
import com.example.emergencyassistb4b4.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class KakaoIndSignupStrategy implements SignUpStrategy {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final TokenService tokenService;

    @Override
    public boolean supports(UserRole userRole, LoginType loginType) {
        return loginType == LoginType.KAKAO && userRole == UserRole.IND;
    }

    @Override
    public TokenResponseDto signUp(SignUpRequestDto requestDto) {
        User user = User.builder()
                .email(requestDto.getEmail())
                .nickname(requestDto.getName())
                .loginType(LoginType.KAKAO)
                .userRole(UserRole.IND)
                .build();
        userRepository.save(user);

        return tokenService.issueToken(user);
    }
}

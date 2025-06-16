package com.example.emergencyassistb4b4.auth.signup.strategy;

import com.example.emergencyassistb4b4.auth.dto.SignUpRequestDto;
import com.example.emergencyassistb4b4.auth.dto.TokenResponseDto;
import com.example.emergencyassistb4b4.auth.token.TokenService;
import com.example.emergencyassistb4b4.user.domain.LoginType;
import com.example.emergencyassistb4b4.user.domain.User;
import com.example.emergencyassistb4b4.user.domain.UserRole;
import com.example.emergencyassistb4b4.user.dto.UserResponseDto;
import com.example.emergencyassistb4b4.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GovSignUpStrategy implements SignUpStrategy {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final TokenService tokenService;

    @Override
    public boolean supports(UserRole userRole, LoginType loginType) {
        return userRole == UserRole.GOV && loginType == LoginType.LOCAL;
    }

    @Override
    public TokenResponseDto signUp(SignUpRequestDto requestDto) {
// 공공 회원가입 로직 구현
        User user = User.builder()
                .nickname(requestDto.getName())
                .email(requestDto.getEmail())
                .password(bCryptPasswordEncoder.encode(requestDto.getPassword()))
                .organizationName(requestDto.getOrganizationName())
                .phoneNumber(requestDto.getPhoneNumber())
                .businessNumber(requestDto.getBusinessNumber())
                .loginType(LoginType.LOCAL)
                .userRole(UserRole.GOV)
                .build();
        userRepository.save(user);
        return tokenService.issueToken(UserResponseDto.from(user));
    }
}

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
public class NgoSignUpStrategy implements SignUpStrategy {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final TokenService tokenService;

    @Override
    public boolean supports(UserRole userRole, LoginType loginType) {
        return userRole == UserRole.NGO && loginType == LoginType.LOCAL;
    }

    @Override
    public TokenResponseDto signUp(SignUpRequestDto requestDto) {
        User user = User.builder()
                .email(requestDto.getEmail())
                .password(bCryptPasswordEncoder.encode(requestDto.getPassword()))
                .organizationName(requestDto.getOrganizationName())
                .businessNumber(requestDto.getBusinessNumber())
                .phoneNumber(requestDto.getPhoneNumber())
                .loginType(LoginType.LOCAL)
                .userRole(UserRole.NGO)
                .build();
        userRepository.save(user);
        return tokenService.issueToken(UserResponseDto.from(user));
    }
}

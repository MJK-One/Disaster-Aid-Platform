package com.example.emergencyassistb4b4.auth.signup.strategy;

import com.example.emergencyassistb4b4.auth.dto.TokenResponseDto;
import com.example.emergencyassistb4b4.auth.signup.SignUpRequestDto;
import com.example.emergencyassistb4b4.auth.token.TokenService;
import com.example.emergencyassistb4b4.global.exception.ApiException;
import com.example.emergencyassistb4b4.global.status.ErrorStatus;
import com.example.emergencyassistb4b4.user.domain.LoginType;
import com.example.emergencyassistb4b4.user.domain.User;
import com.example.emergencyassistb4b4.user.domain.UserRole;
import com.example.emergencyassistb4b4.user.dto.UserResponseDto;
import com.example.emergencyassistb4b4.user.repository.UserRepository;
import io.micrometer.common.util.StringUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GoogleSignupStrategy implements SignUpStrategy {
    private final UserRepository userRepository;
    private final TokenService tokenService;

    @Override
    public boolean supports(UserRole userRole, LoginType loginType) {
        return loginType == LoginType.GOOGLE && userRole == UserRole.IND;
    }

    @Override
    public TokenResponseDto signUp(SignUpRequestDto requestDto) {
        if (StringUtils.isEmpty(requestDto.getEmail())) {
            throw new ApiException(ErrorStatus.INVALID_SIGNUP_REQUEST);
        }
        if (userRepository.existsByEmail(requestDto.getEmail())) {
            throw new ApiException(ErrorStatus.DUPLICATED_EMAIL);
        }

        User user = User.builder()
                .email(requestDto.getEmail())
                .nickname(requestDto.getName())
                .loginType(LoginType.GOOGLE)
                .userRole(UserRole.IND)
                .build();

        userRepository.save(user);
        return tokenService.issueToken(UserResponseDto.from(user));
    }
}

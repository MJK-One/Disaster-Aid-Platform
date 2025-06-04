package com.example.emergencyassistb4b4.user.signupstrategy;

import com.example.emergencyassistb4b4.user.domain.LoginType;
import com.example.emergencyassistb4b4.user.domain.User;
import com.example.emergencyassistb4b4.user.domain.UserRole;
import com.example.emergencyassistb4b4.user.dto.SignUpRequest;
import com.example.emergencyassistb4b4.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrganizationLocalSignUpStrategy implements SignUpStrategy {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    @Override
    public boolean supports(UserRole userRole, LoginType loginType) {
        return userRole == UserRole.NGO && loginType == LoginType.LOCAL;
    }

    @Override
    public void signUp(SignUpRequest requestDto) {
        // 민간 회원가입 로직 구현
        User user = User.builder()
                .email(requestDto.getEmail())
                .password(bCryptPasswordEncoder.encode(requestDto.getPassword()))
                .nickname(requestDto.getOrganizationName())
                .phoneNumber(requestDto.getPhoneNumber())
                .loginType(LoginType.LOCAL)
                .userRole(UserRole.NGO)
                .build();
        userRepository.save(user);
    }
}

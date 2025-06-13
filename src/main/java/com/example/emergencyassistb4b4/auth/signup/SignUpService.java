package com.example.emergencyassistb4b4.auth.signup;

import com.example.emergencyassistb4b4.auth.dto.SignUpRequestDto;
import com.example.emergencyassistb4b4.auth.dto.TokenResponseDto;
import com.example.emergencyassistb4b4.auth.signup.strategy.SignUpStrategy;
import com.example.emergencyassistb4b4.global.exception.ApiException;
import com.example.emergencyassistb4b4.global.status.ErrorStatus;
import com.example.emergencyassistb4b4.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SignUpService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final List<SignUpStrategy>  strategies;
    @Transactional
    public TokenResponseDto signup(SignUpRequestDto requestDto) {
        return strategies.stream()
                .filter(strategy -> strategy.supports(requestDto.getUserRole(), requestDto.getLoginType()))
                .findFirst()
                .orElseThrow( () -> new ApiException(ErrorStatus.SIGNUP_STRATEGY_NOT_FOUND))
                .signUp(requestDto);
    }



}

package com.example.emergencyassistb4b4.user.service;

import com.example.emergencyassistb4b4.global.exception.ApiException;
import com.example.emergencyassistb4b4.global.status.ErrorStatus;
import com.example.emergencyassistb4b4.user.domain.User;
import com.example.emergencyassistb4b4.user.dto.SignUpRequest;
import com.example.emergencyassistb4b4.user.dto.UserResponse;
import com.example.emergencyassistb4b4.user.repository.UserRepository;
import com.example.emergencyassistb4b4.user.signupstrategy.SignUpStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final List<SignUpStrategy> signUpStrategies;


    @Override
    public void signUp(SignUpRequest requestDto) {
        validateEmail(requestDto.getEmail());
        signUpStrategies.stream()
                .filter(strategy -> strategy.supports(requestDto.getUserRole(), requestDto.getLoginType()))
                .findFirst()
                .orElseThrow(() -> new ApiException(ErrorStatus.SIGNUP_STRATEGY_NOT_FOUND))
                .signUp(requestDto);
    }

    @Override
    public void validateEmail(String email) {
        if(userRepository.existsByEmail(email)) {
            throw new ApiException(ErrorStatus.DUPLICATED_EMAIL);
        }
    }

    @Override
    public UserResponse validateUserCredentials(String email, String password) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(()-> new ApiException(ErrorStatus.USER_NOT_FOUND));
        if(!bCryptPasswordEncoder.matches(password, user.getPassword())) {
            throw new ApiException(ErrorStatus.INVALID_PASSWORD);
        }
        return new UserResponse(user.getId(), user.getPassword());
    }


}

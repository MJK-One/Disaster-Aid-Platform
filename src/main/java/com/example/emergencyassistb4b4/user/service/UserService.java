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


public interface UserService {

    public void signUp(SignUpRequest requestDto);
    //이메일 중복 검사
    public void validateEmail(String email);

    public UserResponse validateUserCredentials(String email, String password);


}

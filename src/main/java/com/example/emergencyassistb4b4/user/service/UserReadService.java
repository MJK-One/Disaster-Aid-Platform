package com.example.emergencyassistb4b4.user.service;

import com.example.emergencyassistb4b4.global.exception.ApiException;
import com.example.emergencyassistb4b4.global.status.ErrorStatus;
import com.example.emergencyassistb4b4.user.domain.User;
import com.example.emergencyassistb4b4.user.dto.UserResponse;
import com.example.emergencyassistb4b4.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UserReadService {
    private final UserRepository userRepository;
    public UserResponse findById(long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new ApiException(ErrorStatus.USER_NOT_FOUND));
        return new UserResponse(user.getId(), user.getEmail());
    }


    public UserResponse findByEmail(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new ApiException(ErrorStatus.USER_NOT_FOUND));

        return new UserResponse(user.getId(), user.getEmail());
    }
}

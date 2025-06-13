package com.example.emergencyassistb4b4.user.service;

import com.example.emergencyassistb4b4.global.exception.ApiException;
import com.example.emergencyassistb4b4.global.status.ErrorStatus;
import com.example.emergencyassistb4b4.user.domain.User;
import com.example.emergencyassistb4b4.user.dto.UserResponseDto;
import com.example.emergencyassistb4b4.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {


    private final UserRepository userRepository;

    public UserResponseDto getMyInfo( String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow( () -> new ApiException(ErrorStatus.USER_NOT_FOUND));
        return UserResponseDto.from(user);
    }



}

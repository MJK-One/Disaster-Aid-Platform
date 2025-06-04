package com.example.emergencyassistb4b4.report.dto;

import com.example.emergencyassistb4b4.user.domain.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.web.bind.annotation.GetMapping;

@Getter
@Builder
@AllArgsConstructor
public class UserInfoResponseDto {

    private final String name;

    private final String email;

    private final String phoneNumber;

    public static UserInfoResponseDto from(User user) {

        return UserInfoResponseDto.builder()
                .name(user.getName())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .build();
    }
}

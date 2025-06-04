package com.example.emergencyassistb4b4.user.dto;

import com.example.emergencyassistb4b4.user.domain.LoginType;
import com.example.emergencyassistb4b4.user.domain.User;
import com.example.emergencyassistb4b4.user.domain.UserRole;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserResponse {
    private Long id;
    private String email;

    public static UserResponse of(User user) {
        return new UserResponse(user.getId(), user.getEmail());
    }
}

package com.example.emergencyassistb4b4.auth.oauth.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class KakaoUserDetailsDto {
    private Long id; // 카카오 고유 ID
    private String nickname;
    private String email;


}

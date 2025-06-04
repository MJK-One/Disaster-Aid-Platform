package com.example.demo.domain.location.dto.response;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor

public class ShelterResponseDto {
    private String name;
    private String address;
    private int capacity; // 예시로 포함 (실제로는 카카오 API에 없으면 0 처리)
    private double latitude;
    private double longitude;
}

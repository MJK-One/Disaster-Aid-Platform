package com.example.emergencyassistb4b4.location.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RegionRequestDto {
    // userId는 삭제예정
    private Long userId;
    private String si;
    private String gu;
}
package com.example.emergencyassistb4b4.report.dto;

import com.example.emergencyassistb4b4.alert.enums.DisasterType;
import com.example.emergencyassistb4b4.report.domain.Report;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
// 위도,경도,시,구를 프론트에서 가져오기(일회성이라서)
public class ReportRequestDto {

    private DisasterType disasterType;

    private String description;

    private String imageUrl;

    private String videoUrl;

}

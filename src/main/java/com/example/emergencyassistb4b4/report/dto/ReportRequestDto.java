package com.example.emergencyassistb4b4.report.dto;

import com.example.emergencyassistb4b4.alert.enums.DisasterType;
import com.example.emergencyassistb4b4.report.domain.Report;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class ReportRequestDto {

    private final DisasterType disasterType;

    private final String description;

    private final String imageUrl;

    private final String videoUrl;

    public static ReportRequestDto from(Report report) {

        return ReportRequestDto.builder()
                .disasterType(report.getDisasterType())
                .description(report.getDescription())
                .imageUrl(report.getImageUrl())
                .videoUrl(report.getVideoUrl())
                .build();
    }

}

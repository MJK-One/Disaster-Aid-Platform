package com.example.emergencyassistb4b4.report.dto;

import com.example.emergencyassistb4b4.alert.enums.DisasterType;
import com.example.emergencyassistb4b4.report.domain.Report;
import com.example.emergencyassistb4b4.report.domain.ReportResponse;
import com.example.emergencyassistb4b4.report.enums.ReportStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
public class ReportResponseDto {

    private final Long reporter;

    private final DisasterType disasterType;

    private final String description;

    private final String imageUrl;

    private final String videoUrl;

    private final ReportStatus status;

    private final String si;

    private final String gu;

    private final Double locationLat; // 위도

    private final Double locationLng; // 경도

    private final LocalDateTime updatedAt;

    public static ReportResponseDto from(Report report) {

        return ReportResponseDto.builder()
                .reporter(report.getReporter().getId())
                .disasterType(report.getDisasterType())
                .description(report.getDescription())
                .imageUrl(report.getImageUrl())
                .videoUrl(report.getVideoUrl())
                .status(report.getStatus())
                .si(report.getSi())
                .gu(report.getGu())
                .locationLat(report.getLocationLat())
                .locationLng(report.getLocationLng())
                .updatedAt(report.getUpdatedAt())
                .build();
    }
}

package com.example.emergencyassistb4b4.global.kafka.dto;

import com.example.emergencyassistb4b4.report.domain.Report;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DisasterAlertMessage { // kafka 메세지 Dto

    private Long reportId;

    private String disasterType; // Enum -> String 변환

    private String location;

    private Long reporterId;

    private LocalDateTime reportedAt;

    public static DisasterAlertMessage from(Report report) {

        return DisasterAlertMessage.builder()
                .reportId(report.getId())
                .disasterType(report.getDisasterType().name())
                .location(report.getSi() + " " + report.getGu())
                .reporterId(report.getReporter().getId())
                .reportedAt(report.getCreatedAt())
                .build();
    }
}

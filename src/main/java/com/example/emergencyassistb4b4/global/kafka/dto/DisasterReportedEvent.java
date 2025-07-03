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
public class DisasterReportedEvent { // Kafka 전용 재난 알림 메시지 DTO

    private Long reportId;

    private Long reporterId;

    private Long responderId;

    private String disasterType;

    private String description;

    private String si;

    private String gu;

    private LocalDateTime reportedAt;

    public static DisasterReportedEvent from(Report report) {

        return DisasterReportedEvent.builder()
            .reportId(report.getId())
            .reporterId(report.getReporter().getId())
            .responderId(report.getResponder().getId())
            .disasterType(report.getDisasterType().getName())
            .description(report.getDescription())
            .si(report.getProvince())
            .gu(report.getCity())
            .reportedAt(report.getCreatedAt())
            .build();
    }
}

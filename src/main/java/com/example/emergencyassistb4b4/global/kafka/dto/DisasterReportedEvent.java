package com.example.emergencyassistb4b4.global.kafka.dto;

import com.example.emergencyassistb4b4.report.domain.Report;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DisasterReportedEvent {

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
            .si(report.getSi())
            .gu(report.getGu())
            .reportedAt(report.getCreatedAt())
            .build();
    }
}

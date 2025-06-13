package com.example.emergencyassistb4b4.alert.dto.report;

import com.example.emergencyassistb4b4.alert.enums.DisasterType;
import com.example.emergencyassistb4b4.report.domain.Report;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ReportImmediateAlertDto {

    private String si;
    private String gu;
    private DisasterType disasterType;

    public static ReportImmediateAlertDto fromReport(Report report) {

        return ReportImmediateAlertDto.builder()
            .si(report.getSi())
            .gu(report.getGu())
            .disasterType(report.getDisasterType())
            .build();
    }
}

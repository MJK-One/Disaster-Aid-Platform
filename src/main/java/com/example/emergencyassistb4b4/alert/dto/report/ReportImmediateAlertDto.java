package com.example.emergencyassistb4b4.alert.dto.report;

import com.example.emergencyassistb4b4.global.kafka.dto.DisasterReportedEvent;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ReportImmediateAlertDto {

    private String si;
    private String gu;
    private String disasterType;

    public static ReportImmediateAlertDto fromEvent(DisasterReportedEvent event) {

        return ReportImmediateAlertDto.builder()
            .si(event.getProvince())
            .gu(event.getCity())
            .disasterType(event.getDisasterType())
            .build();
    }
}

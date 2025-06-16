package com.example.emergencyassistb4b4.alert.dto.report;

import com.example.emergencyassistb4b4.alert.enums.DisasterType;
import com.example.emergencyassistb4b4.global.kafka.dto.DisasterAlertMessage;
import com.example.emergencyassistb4b4.report.domain.Report;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ReportImmediateAlertDto {

    private String si;
    private String gu;
    private DisasterType disasterType;

    public static ReportImmediateAlertDto fromReport(DisasterAlertMessage kafkaDto) {

        String[] location = kafkaDto.getLocation().split(" ");

        return ReportImmediateAlertDto.builder()
            .si(location[0])
            .gu(location[1])
            .disasterType(DisasterType.from(kafkaDto.getDisasterType()))
            .build();
    }
}

package com.example.emergencyassistb4b4.location.dto.response;

import com.example.emergencyassistb4b4.alert.enums.DisasterType;
import com.example.emergencyassistb4b4.report.enums.ReportStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class DisasterReportSimpleDto {
    private DisasterType disasterType;
    private ReportStatus status;
    private double latitude;
    private double longitude;

    public DisasterReportSimpleDto(String disasterType, String status, double latitude, double longitude) {
        this.disasterType = DisasterType.valueOf(disasterType);
        this.status = ReportStatus.valueOf(status);
        this.latitude = latitude;
        this.longitude = longitude;
    }
}

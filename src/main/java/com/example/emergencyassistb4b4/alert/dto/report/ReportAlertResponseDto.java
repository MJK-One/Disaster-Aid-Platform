package com.example.emergencyassistb4b4.alert.dto.report;

import com.example.emergencyassistb4b4.alert.domain.report.ReportAlert;
import com.example.emergencyassistb4b4.alert.domain.report.UserReportAlert;
import com.example.emergencyassistb4b4.alert.dto.response.UserAlert;
import com.example.emergencyassistb4b4.alert.enums.DisasterType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class ReportAlertResponseDto implements UserAlert {

    private Long id;
    private String si;
    private String gu;
    private DisasterType disasterType;
    private Long count;

    public static ReportAlertResponseDto fromUserReportAlert(UserReportAlert userReportAlert) {
        ReportAlert reportAlert = userReportAlert.getReportAlert();

        return ReportAlertResponseDto.builder()
            .id(userReportAlert.getId())
            .si(reportAlert.getSi())
            .gu(reportAlert.getGu())
            .disasterType(reportAlert.getDisasterType())
            .count(reportAlert.getCount())
            .build();
    }
}

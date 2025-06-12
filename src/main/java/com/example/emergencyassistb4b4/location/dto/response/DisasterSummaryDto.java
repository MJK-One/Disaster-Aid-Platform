package com.example.emergencyassistb4b4.location.dto.response;

import com.example.emergencyassistb4b4.alert.enums.DisasterType;
import com.example.emergencyassistb4b4.report.enums.ReportStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class DisasterSummaryDto {

    private DisasterType disasterType;
    private ReportStatus status;
    private long count;         // 해당 유형 + 상태 재난 신고 건수
    private double latitude;
    private double longitude;

    public static DisasterSummaryDto from(Object[] row) {
        return DisasterSummaryDto.builder()
                .disasterType((DisasterType) row[0])
                .status((ReportStatus) row[1])
                .count(((Number) row[2]).longValue())
                .latitude((Double) row[3])
                .longitude((Double) row[4])
                .build();
    }
}



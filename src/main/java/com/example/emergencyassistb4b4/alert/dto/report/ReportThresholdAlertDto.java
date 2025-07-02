package com.example.emergencyassistb4b4.alert.dto.report;

import static com.example.emergencyassistb4b4.global.status.ErrorStatus.CUSTOM_ERROR_STATUS;

import com.example.emergencyassistb4b4.alert.domain.report.ReportAlert;
import com.example.emergencyassistb4b4.report.enums.DisasterType;
import com.example.emergencyassistb4b4.global.exception.ApiException;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ReportThresholdAlertDto {

    private String si;
    private String gu;
    private DisasterType disasterType;
    private Long count;

    private static final String PREFIX  = "alert";
    private static final int IDX_SI   = 2;
    private static final int IDX_GU   = 3;
    private static final int IDX_TYPE = 4;
    private static final int IDX_CNT  = 6;

    public static ReportThresholdAlertDto fromKey(String notifyKey) {

        String[] parts = notifyKey.split(":");

        // 키 검증
        if (!PREFIX.equals(parts[0])) {
            throw new ApiException(CUSTOM_ERROR_STATUS);
        }

        return ReportThresholdAlertDto.builder()
            .si(parts[IDX_SI])
            .gu(parts[IDX_GU])
            .disasterType(DisasterType.from(parts[IDX_TYPE]))
            .count(Long.parseLong(parts[IDX_CNT]))
            .build();
    }

    public ReportAlert toEntity() {
        return ReportAlert.builder()
            .si(this.si)
            .gu(this.gu)
            .disasterType(this.disasterType)
            .count(this.count)
            .build();
    }
}

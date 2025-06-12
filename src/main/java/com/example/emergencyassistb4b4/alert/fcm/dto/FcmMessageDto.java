package com.example.emergencyassistb4b4.alert.fcm.dto;

import com.example.emergencyassistb4b4.alert.dto.report.ReportThresholdAlertDto;
import com.example.emergencyassistb4b4.alert.dto.report.ReportImmediateAlertDto;
import com.example.emergencyassistb4b4.alert.dto.volunteer.VolunteerUpdateAlertDto;
import java.time.format.DateTimeFormatter;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class FcmMessageDto {

    private String title;
    private String body;

    public static FcmMessageDto fromReportThresholdAlert(ReportThresholdAlertDto alert) {

        String title = String.format(
            "[재난 알림] %s %s %s 발생 알림",
            alert.getSi(), alert.getGu(), alert.getDisasterType().getName()
        );
        String body = String.format(
            "%s %s에서 %s 신고가 %s건 이상 접수되었습니다.",
            alert.getSi(), alert.getGu(), alert.getDisasterType().getName(), alert.getCount()
        );

        return FcmMessageDto.builder()
            .title(title)
            .body(body)
            .build();
    }

    public static FcmMessageDto fromReportImmediateAlert(ReportImmediateAlertDto alert) {
        String title = String.format(
            "[재난 신고 접수] %s %s %s 신고",
            alert.getSi(), alert.getGu(), alert.getDisasterType().getName()
        );
        String body = String.format(
            "%s %s에서 %s 신고가 접수되었습니다.",
            alert.getSi(), alert.getGu(), alert.getDisasterType().getName()
        );

        return FcmMessageDto.builder()
            .title(title)
            .body(body)
            .build();
    }

    public static FcmMessageDto fromVolunteerUpdateAlert(VolunteerUpdateAlertDto alert) {

        String title = String.format(
            "[봉사 알림] %s 변경 공지", alert.getTitle()
        );
        String body = String.format(
                """
                제목 : %s
                장소 : %s
                시간 : %s
                """,
            alert.getTitle(),
            alert.getLocation(),
            alert.getStartTime().format(DateTimeFormatter.ofPattern("MM월 dd일 HH:mm"))
        );

        return FcmMessageDto.builder()
            .title(title)
            .body(body)
            .build();
    }
}

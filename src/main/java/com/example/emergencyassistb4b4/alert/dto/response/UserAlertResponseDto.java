package com.example.emergencyassistb4b4.alert.dto.response;

import com.example.emergencyassistb4b4.alert.domain.report.ReportAlert;
import com.example.emergencyassistb4b4.alert.domain.report.UserReportAlert;
import com.example.emergencyassistb4b4.alert.domain.volunteer.UserVolunteerAlert;
import com.example.emergencyassistb4b4.alert.domain.volunteer.VolunteerAlert;
import com.example.emergencyassistb4b4.alert.enums.DisasterType;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

public class UserAlertResponseDto {

    public interface UserAlert {}

    @Getter
    @Builder
    @AllArgsConstructor
    public static class Report implements UserAlert {

        private Long id;
        private String si;
        private String gu;
        private DisasterType disasterType;
        private Long count;

        public static Report fromUserReportAlert(UserReportAlert userReportAlert) {
            ReportAlert reportAlert = userReportAlert.getReportAlert();

            return Report.builder()
                .id(userReportAlert.getId())
                .si(reportAlert.getSi())
                .gu(reportAlert.getGu())
                .disasterType(reportAlert.getDisasterType())
                .count(reportAlert.getCount())
                .build();
        }
    }

    @Getter
    @Builder
    @AllArgsConstructor
    public static class Volunteer implements UserAlert {

        private Long id;
        private String title;
        private String location;
        private LocalDateTime startTime;

        public static Volunteer fromUserVolunteerAlert(UserVolunteerAlert userVolunteerAlert) {
            VolunteerAlert volunteerAlert = userVolunteerAlert.getVolunteerAlert();

            return Volunteer.builder()
                .id(userVolunteerAlert.getId())
                .title(volunteerAlert.getTitle())
                .location(volunteerAlert.getLocation())
                .startTime(volunteerAlert.getStartTime())
                .build();
        }
    }
}

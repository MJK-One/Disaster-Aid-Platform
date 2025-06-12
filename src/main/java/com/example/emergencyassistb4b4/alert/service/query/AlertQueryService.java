package com.example.emergencyassistb4b4.alert.service.query;

import com.example.emergencyassistb4b4.alert.dto.response.UserAlertResponseDto.UserAlert;
import com.example.emergencyassistb4b4.alert.dto.response.UserAlertResponseDto.Report;
import com.example.emergencyassistb4b4.alert.dto.response.UserAlertResponseDto.Volunteer;
import com.example.emergencyassistb4b4.alert.enums.AlertType;
import com.example.emergencyassistb4b4.alert.repository.report.UserReportAlertRepository;
import com.example.emergencyassistb4b4.alert.repository.volunteer.UserVolunteerAlertRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AlertQueryService {

    private final UserReportAlertRepository userReportAlertRepository;
    private final UserVolunteerAlertRepository userVolunteerAlertRepository;

    // 알림 조회
    public List<UserAlert> listAlerts(AlertType type, Long userId) {

        return switch (type) {
            // 재난 알림
            case DISASTER -> listDisasterAlerts(userId);
            // 봉사 알림
            case VOLUNTEER -> listVolunteerAlerts(userId);
        };
    }

    // 재난 알림 조회
    private List<UserAlert> listDisasterAlerts(Long userId) {

        return userReportAlertRepository
            .findByUser_IdOrderByIdDesc(userId)
            .stream()
            .map(alert -> (UserAlert) Report.fromUserReportAlert(alert))
            .toList();
    }

    // 봉사 알림 조회
    private List<UserAlert> listVolunteerAlerts(Long userId) {

        return userVolunteerAlertRepository
            .findByUser_IdOrderByIdDesc(userId)
            .stream()
            .map(alert -> (UserAlert) Volunteer.fromUserVolunteerAlert(alert))
            .toList();
    }
}

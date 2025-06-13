package com.example.emergencyassistb4b4.alert.service.command;

import com.example.emergencyassistb4b4.alert.domain.report.ReportAlert;
import com.example.emergencyassistb4b4.alert.domain.report.UserReportAlert;
import com.example.emergencyassistb4b4.alert.domain.volunteer.UserVolunteerAlert;
import com.example.emergencyassistb4b4.alert.domain.volunteer.VolunteerAlert;
import com.example.emergencyassistb4b4.alert.dto.report.ReportThresholdAlertDto;
import com.example.emergencyassistb4b4.alert.dto.volunteer.VolunteerUpdateAlertDto;
import com.example.emergencyassistb4b4.alert.repository.report.ReportAlertRepository;
import com.example.emergencyassistb4b4.alert.repository.report.UserReportAlertRepository;
import com.example.emergencyassistb4b4.alert.repository.volunteer.UserVolunteerAlertRepository;
import com.example.emergencyassistb4b4.alert.repository.volunteer.VolunteerAlertRepository;
import com.example.emergencyassistb4b4.user.domain.User;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class AlertCommandService {

    private final ReportAlertRepository reportAlertRepository;
    private final UserReportAlertRepository userReportAlertRepository;
    private final VolunteerAlertRepository volunteerAlertRepository;
    private final UserVolunteerAlertRepository userVolunteerAlertRepository;

    public void saveReportAlert(ReportThresholdAlertDto dto, List<User> users) {

        // 1. ReportAlert 생성 및 저장
        ReportAlert alert = reportAlertRepository.save(dto.toEntity());

        // 2. UserReportAlert 생성 및 일괄 저장
        userReportAlertRepository.saveAll(UserReportAlert.fromUsers(alert, users));

    }

    public void saveVolunteerAlert(VolunteerUpdateAlertDto dto, List<User> users) {

        // 1. ReportAlert 생성 및 저장
        VolunteerAlert alert = volunteerAlertRepository.save(dto.toEntity());

        // 2. UserReportAlert 생성 및 일괄 저장
        userVolunteerAlertRepository.saveAll(UserVolunteerAlert.fromUsers(alert, users));

    }
}
package com.example.emergencyassistb4b4.report.service;

import com.example.emergencyassistb4b4.global.exception.ApiException;
import com.example.emergencyassistb4b4.global.status.ErrorStatus;
import com.example.emergencyassistb4b4.report.domain.Report;
import com.example.emergencyassistb4b4.report.domain.ReportResponse;
import com.example.emergencyassistb4b4.report.dto.ReportRequestDto;
import com.example.emergencyassistb4b4.report.dto.ReportResponseDto;
import com.example.emergencyassistb4b4.report.enums.ReportStatus;
import com.example.emergencyassistb4b4.report.repository.ReportRepository;
import com.example.emergencyassistb4b4.report.repository.ReportResponseRepository;
import com.example.emergencyassistb4b4.user.domain.User;
import com.example.emergencyassistb4b4.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final ReportRepository reportRepository;
    private final ReportResponseRepository reportResponseRepository;

    // (사용자) 재난 신고 기능
    public ReportResponseDto disasterReport(ReportRequestDto requestDto, User reporter) {

        Report report = Report.builder()
                .reporter(reporter)
                .disasterType(requestDto.getDisasterType())
                .description(requestDto.getDescription())
                .imageUrl(requestDto.getImageUrl())
                .videoUrl(requestDto.getVideoUrl())
                .status(ReportStatus.PENDING)
                .si("서울시") // 예시: 위치 서비스로 가져온 값
                .gu("강남구")
                .locationLat(BigDecimal.valueOf(37.5665))
                .locationLng(BigDecimal.valueOf(126.9780))
                .build();

        Report savedReport = reportRepository.save(report);

        return ReportResponseDto.from(savedReport);
    }

    // (공공기관) 재난 신고 내역 조회 기능
    @Transactional(readOnly = true)
    public List<ReportResponseDto> getReportList(User responder) {

        List<ReportResponse> reportResponses = reportResponseRepository.findByResponder(responder);

        return reportResponses.stream()
                .map(reportResponse -> {
                    Report report = reportResponse.getReport();

                    return ReportResponseDto.builder()
                            .reporter(report.getReporter().getId()) // 신고자 아이디
                            .disasterType(report.getDisasterType())
                            .description(report.getDescription())
                            .imageUrl(report.getImageUrl())
                            .videoUrl(report.getVideoUrl())
                            .status(report.getStatus())
                            .si(report.getSi()) // 예시: 위치 서비스로 가져온 값
                            .gu(report.getGu())
                            .locationLat(report.getLocationLat())
                            .locationLng(report.getLocationLng())
                            .build();
                })
                .collect(Collectors.toList());
    }
}

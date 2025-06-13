package com.example.emergencyassistb4b4.report.service;

import com.example.emergencyassistb4b4.global.kafka.dto.DisasterAlertMessage;
import com.example.emergencyassistb4b4.global.kafka.producer.DisasterAlertProducer;
import com.example.emergencyassistb4b4.location.service.LocationService;
import com.example.emergencyassistb4b4.report.domain.Report;
import com.example.emergencyassistb4b4.report.domain.ReportResponse;
import com.example.emergencyassistb4b4.global.kafka.dto.DisasterAlertMessage;
import com.example.emergencyassistb4b4.report.dto.ReportDto;
import com.example.emergencyassistb4b4.report.dto.ReportRequestDto;
import com.example.emergencyassistb4b4.report.dto.ReportResponseDto;
import com.example.emergencyassistb4b4.report.dto.ReportStatusResponseDto;
import com.example.emergencyassistb4b4.report.enums.ReportStatus;
import com.example.emergencyassistb4b4.report.repository.ReportRepository;
import com.example.emergencyassistb4b4.report.repository.ReportResponseRepository;
import com.example.emergencyassistb4b4.user.domain.User;
import com.example.emergencyassistb4b4.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final ReportRepository reportRepository;
    private final ReportResponseRepository reportResponseRepository;
    private final DisasterAlertProducer disasterAlertProducer;
    private final UserRepository userRepository;

    // (사용자) 재난 신고 기능
    @Transactional
    public ReportResponseDto disasterReport(ReportRequestDto requestDto, User reporter) {


        // 신고 저장
        Report report = Report.builder()
                .reporter(reporter)
                .disasterType(requestDto.getDisasterType())
                .description(requestDto.getDescription())
                .imageUrl(requestDto.getImageUrl())
                .videoUrl(requestDto.getVideoUrl())
                .status(ReportStatus.PENDING)
                .si("서울시") // 예시: 위치 서비스로 가져온 값
                .gu("강남구")
                .locationLat(Double.valueOf(37.5665))
                .locationLng(Double.valueOf(126.9780))
                .build();

        Report savedReport = reportRepository.save(report);

        // kafka 메세지 발행
        DisasterAlertMessage alertMessage = DisasterAlertMessage.from(savedReport);

        disasterAlertProducer.sendDisasterAlert(alertMessage);

        // Dto 반환
        return ReportResponseDto.from(savedReport);
    }

    // (공공기관) 재난 신고 내역 조회 기능
    @Transactional(readOnly = true)
    public List<ReportResponseDto> getReportList(User responder) {

        List<ReportResponse> reportResponses = reportResponseRepository.findByResponder(responder);

        return reportResponses.stream()
                .map(reportResponse -> ReportResponseDto.from(reportResponse.getReport()))
                .collect(Collectors.toList());
    }


    /** 공공기관 단건 상태변경 */
    @PreAuthorize("hasRole('GOV')")
    @Transactional
    public ReportStatusResponseDto changeReportStatus(
            Long publicId, //공공기관 Id
            Long reportId,
            ReportStatus newStatus){

        /* 공공기관인지 검증 */
        User goverment = userRepository.findById(publicId).orElseThrow(); //user error 넣기
        // Report 조회
        Report r = reportRepository.findById(reportId).orElseThrow(
                ()->new IllegalStateException("조회된 신고가 없습니다.")); //예외 컨벤션 만들기

        //상태 변경
        r.updateStatus(newStatus);
        return new ReportStatusResponseDto(reportId,newStatus);
    }
    // 공공기관 상태변경 (다건)

    // 주변 신고 목록 조회
    @PreAuthorize("hasRole('GOV')")
    @Transactional(readOnly = true)
    public Slice<ReportDto> getNearbyReports(String si, String gu, ReportStatus status, Pageable pageable) {
        return reportRepository.findNearby(si, gu, status, pageable).map(ReportDto::of);
    }

    //내 신고 목록 조회 (신고한 유저의 목록)
    @Transactional(readOnly = true)
    public Slice<ReportDto> getMyReports(
            Long userId, ReportStatus status, LocalDateTime start,
            LocalDateTime end, Pageable pageable){
        return reportRepository.findByReporter(userId, status, start, end, pageable)
                .map(ReportDto::of);
    }

}

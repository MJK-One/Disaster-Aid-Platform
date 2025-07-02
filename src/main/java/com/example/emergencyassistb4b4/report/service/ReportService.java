package com.example.emergencyassistb4b4.report.service;

import com.example.emergencyassistb4b4.alert.service.report.ReportImmediateAlertOrchestratorService;
import com.example.emergencyassistb4b4.alert.service.report.ReportThresholdAlertTriggerService;
import com.example.emergencyassistb4b4.global.S3.S3Uploader;
import com.example.emergencyassistb4b4.global.exception.ApiException;
import com.example.emergencyassistb4b4.global.kafka.dto.DisasterAlertMessage;
import com.example.emergencyassistb4b4.global.kafka.producer.DisasterAlertProducer;
import com.example.emergencyassistb4b4.global.status.ErrorStatus;
import com.example.emergencyassistb4b4.global.util.RegionUtils;
import com.example.emergencyassistb4b4.report.domain.Report;
import com.example.emergencyassistb4b4.report.dto.ReportDto;
import com.example.emergencyassistb4b4.report.dto.ReportRequestDto;
import com.example.emergencyassistb4b4.report.dto.ReportResponseDto;
import com.example.emergencyassistb4b4.report.dto.ReportStatusResponseDto;
import com.example.emergencyassistb4b4.report.enums.ReportStatus;
import com.example.emergencyassistb4b4.report.repository.ReportRepository;
import com.example.emergencyassistb4b4.user.domain.User;
import com.example.emergencyassistb4b4.user.domain.UserRole;
import com.example.emergencyassistb4b4.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final ReportRepository reportRepository;
    private final DisasterAlertProducer disasterAlertProducer;
    private final UserRepository userRepository;
    private final ReportImmediateAlertOrchestratorService reportImmediateAlertOrchestratorService;
    private final ReportThresholdAlertTriggerService reportThresholdAlertTriggerService;
    private final S3Uploader s3Uploader;

    // (사용자) 재난 신고 기능
    @Transactional
    public ReportResponseDto disasterReport(ReportRequestDto requestDto, User reporter, MultipartFile image, MultipartFile video) throws IOException {

        String imageUrl = null;
        String videoUrl = null;

        if (image != null && !image.isEmpty()) {
            try {
                imageUrl = s3Uploader.uploadFile(image, "images");
            } catch (IOException e) {
                throw new ApiException(ErrorStatus.S3_UPLOAD_ERROR); // 커스텀 예외
            }
        }

        if (video != null && !video.isEmpty()) {
            try {
                videoUrl = s3Uploader.uploadFile(video, "videos");
            } catch (IOException e) {
                throw new ApiException(ErrorStatus.S3_UPLOAD_ERROR); // 커스텀 예외
            }
        }

        double latitude = requestDto.getLatitude();
        double longitude = requestDto.getLongitude();
        String rawRegion = requestDto.getSi(); // ex. 경기도 광주시, 광주광역시, 서울특별시 등
        String normalizedRegion = RegionUtils.normalizeSi(rawRegion); // 정규화 적용

        // 위도, 경도 -> point
        GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);
        Point location = geometryFactory.createPoint(new Coordinate(longitude, latitude));

        User responder = userRepository.findFirstByUserRoleAndSi(UserRole.GOV, normalizedRegion)
                .orElseThrow(() -> new ApiException(ErrorStatus.GOV_NOT_FOUND));

        // 신고 저장
        Report report = Report.builder()
                .reporter(reporter)
                .disasterType(requestDto.getDisasterType())
                .description(requestDto.getDescription())
                .imageUrl(imageUrl)
                .videoUrl(videoUrl)
                .status(ReportStatus.PENDING)
                .si(normalizedRegion) // 예시: 위치 서비스로 가져온 값
                .gu(requestDto.getGu())
                .location(location)
                .responder(responder)
                .build();

        Report savedReport = reportRepository.save(report);

        // kafka 메세지 발행
        DisasterAlertMessage alertMessage = DisasterAlertMessage.from(savedReport);

        disasterAlertProducer.sendDisasterAlert(alertMessage);

        // FCM 즉시 알림
        reportImmediateAlertOrchestratorService.process(alertMessage);

        // FCM 누적 알림
        reportThresholdAlertTriggerService.checkReportThreshold(alertMessage);

        // Dto 반환
        return ReportResponseDto.from(savedReport);
    }

    // (공공기관) 신고 내역 조회 기능
    @Transactional(readOnly = true)
    public List<ReportResponseDto> getReportList(User responder) {

        List<Report> reports = reportRepository.findAllByResponderOrderByCreatedAtDesc(responder);

        return reports.stream()
                .map(ReportResponseDto::from)
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

    public static String extractSiPrefix(String nickname) {

        // 예: "서울시 소방서" → "서울시"
        if (nickname == null || nickname.isBlank()) return "";

        String[] parts = nickname.trim().split("\\s+");

        for (String part : parts) {

            if (part.endsWith("시")) {

                return part;
            }
        }

        return ""; // 못 찾은 경우
    }
}
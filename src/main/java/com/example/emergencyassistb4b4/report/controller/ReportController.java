package com.example.emergencyassistb4b4.report.controller;

import com.example.emergencyassistb4b4.global.response.ApiResponse;
import com.example.emergencyassistb4b4.global.status.SuccessStatus;
import com.example.emergencyassistb4b4.report.dto.ReportRequestDto;
import com.example.emergencyassistb4b4.report.dto.ReportResponseDto;
import com.example.emergencyassistb4b4.report.service.ReportService;
import com.example.emergencyassistb4b4.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    @PostMapping()
    public ResponseEntity<ApiResponse<ReportResponseDto>> disasterReport(
            @RequestBody ReportRequestDto requestDto
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {

        // 로그인 사용자 정보 가져오기
        User currentUser = userDetails.getUser();

        ReportResponseDto responseDto = reportService.disasterReport(requestDto, currentUser);

        return ApiResponse.onSuccess(SuccessStatus.REPORT_CREATE_SUCCESS, responseDto);
    }

    @GetMapping()
    public ResponseEntity<ApiResponse<List<ReportResponseDto>>> getReportList(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {

        // 로그인 사용자 정보 가져오기
        User currentUser = userDetails.getUser();

        List<ReportResponseDto> responseDtos = reportService.getReportList(currentUser);

        return ApiResponse.onSuccess(SuccessStatus.REPORT_GET_SUCCESS, responseDtos);
    }
}

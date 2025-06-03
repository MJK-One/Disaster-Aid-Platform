package com.example.emergencyassistb4b4.report.controller;

import com.example.emergencyassistb4b4.global.response.ApiResponse;
import com.example.emergencyassistb4b4.report.dto.ReportRequestDto;
import com.example.emergencyassistb4b4.report.dto.ReportResponseDto;
import com.example.emergencyassistb4b4.report.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    @PostMapping()
    public ResponseEntity<ApiResponse<ReportResponseDto>> disasterReport(@RequestBody ReportRequestDto requestDto) {

        ReportResponseDto responseDto = reportService.disasterReport(requestDto);

    }


}

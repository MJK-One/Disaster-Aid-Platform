package com.example.emergencyassistb4b4.report.service;

import com.example.emergencyassistb4b4.report.domain.Report;
import com.example.emergencyassistb4b4.report.dto.ReportRequestDto;
import com.example.emergencyassistb4b4.report.dto.ReportResponseDto;
import com.example.emergencyassistb4b4.report.repository.ReportRepository;
import com.example.emergencyassistb4b4.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final UserRepository userRepository;
    private final ReportRepository reportRepository;

    public ReportResponseDto disasterReport(ReportRequestDto requestDto) {



        return new ReportResponseDto()
    }

    public List<ReportResponseDto> reportList() {

        return
    }
}

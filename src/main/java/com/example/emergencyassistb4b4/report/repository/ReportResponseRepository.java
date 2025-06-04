package com.example.emergencyassistb4b4.report.repository;

import com.example.emergencyassistb4b4.report.domain.Report;
import com.example.emergencyassistb4b4.report.domain.ReportResponse;
import com.example.emergencyassistb4b4.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ReportResponseRepository extends JpaRepository<ReportResponse, Long> {

    List<ReportResponse> findByResponder(User responder);

    boolean existsByReportAndResponder(Report report, User responder);

    User responder(User responder);
}

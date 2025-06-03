package com.example.emergencyassistb4b4.report.repository;

import com.example.emergencyassistb4b4.report.domain.Report;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReportRepository extends JpaRepository<Report, Long> {
}

package com.example.emergencyassistb4b4.report.repository;

import com.example.emergencyassistb4b4.report.domain.Report;
import com.example.emergencyassistb4b4.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ReportRepository extends JpaRepository<Report, Long>,ReportRepositoryCustom {

    Optional<Report> findById(Long id);
}

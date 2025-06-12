package com.example.emergencyassistb4b4.alert.repository;

import com.example.emergencyassistb4b4.alert.domain.AlertFailureLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AlertFailureLogRepository extends JpaRepository<AlertFailureLog, Long> {

    boolean existsByReportId(long id);
}

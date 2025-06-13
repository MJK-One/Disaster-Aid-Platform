package com.example.emergencyassistb4b4.report.repository;

import com.example.emergencyassistb4b4.location.dto.response.DisasterReportSimpleDto;
import com.example.emergencyassistb4b4.location.dto.response.DisasterSummaryDto;
import com.example.emergencyassistb4b4.report.domain.Report;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ReportRepository extends JpaRepository<Report, Long> {

    Optional<Report> findById(Long id);

    @Query("""
    SELECT new com.example.emergencyassistb4b4.location.dto.response.DisasterReportSimpleDto(
        r.disasterType,
        r.status,
        r.locationLat,
        r.locationLng
    )
    FROM Report r
    WHERE FUNCTION('ST_Distance_Sphere',
                   FUNCTION('point', :longitude, :latitude),
                   FUNCTION('point', r.locationLng, r.locationLat)) <= :radiusMeter
      AND r.createdAt >= :fromTime
    """)
    List<DisasterReportSimpleDto> findNearbyDisasterReports(
            @Param("latitude") double latitude,
            @Param("longitude") double longitude,
            @Param("radiusMeter") int radiusMeter,
            @Param("fromTime") LocalDateTime fromTime
    );
}

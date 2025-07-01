package com.example.emergencyassistb4b4.report.repository;

import com.example.emergencyassistb4b4.location.dto.response.DisasterReportSimpleDto;
import com.example.emergencyassistb4b4.report.domain.Report;
import com.example.emergencyassistb4b4.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface ReportRepository extends JpaRepository<Report, Long>,ReportRepositoryCustom {

    // responder 기준으로 createdAt 내림차순 정렬
    List<Report> findAllByResponderOrderByCreatedAtDesc(User responder);

    @Query(value = """
    SELECT
        r.disaster_type AS disasterType,
        r.status AS status,
        ST_Y(r.location::geometry) AS locationLat,
        ST_X(r.location::geometry) AS locationLng
    FROM report r
    WHERE ST_DWithin(
        r.location,
        ST_SetSRID(ST_MakePoint(:longitude, :latitude), 4326)::geography,
        :radiusMeter
    )
    AND r.created_at >= :fromTime
""", nativeQuery = true)
    List<DisasterReportSimpleDto> findNearbyDisasterReports(
            @Param("latitude") double latitude,
            @Param("longitude") double longitude,
            @Param("radiusMeter") int radiusMeter,
            @Param("fromTime") LocalDateTime fromTime
    );
}

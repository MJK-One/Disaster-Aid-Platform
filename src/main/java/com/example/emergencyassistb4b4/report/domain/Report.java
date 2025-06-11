package com.example.emergencyassistb4b4.report.domain;

import com.example.emergencyassistb4b4.alert.enums.DisasterType;
import com.example.emergencyassistb4b4.global.entity.BaseEntity;
import com.example.emergencyassistb4b4.report.enums.ReportStatus;
import com.example.emergencyassistb4b4.user.domain.User;
import jakarta.persistence.*;

import java.math.BigDecimal;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(name = "report")
public class Report extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "report_seq_gen")
    @SequenceGenerator(
            name = "report_seq_gen",
            sequenceName = "report_seq", // DB에 시퀀스 직접 샏성 필요
            allocationSize = 50
    )
    private Long id;

    // 신고자 (FK)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reporter_id", nullable = false)
    private User reporter;

    // 재난 유형
    @Enumerated(EnumType.STRING)
    @Column(name = "disaster_type", nullable = false)
    private DisasterType disasterType;

    // 설명
    @Lob
    @Column(name = "description")
    private String description;

    // 이미지 URL
    @Column(name = "image_url", length = 255)
    private String imageUrl;

    // 비디오 URL
    @Column(name = "video_url", length = 255)
    private String videoUrl;

    // 상태
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ReportStatus status;

    // 행정구역 (시)
    @Column(name = "si", nullable = false, length = 255)
    private String si;

    // 행정구역 (구)
    @Column(name = "gu", nullable = false, length = 255)
    private String gu;

    // 위도
    @Column(name = "location_lat")
    private Double locationLat;

    // 경도
    @Column(name = "location_lng")
    private Double locationLng;
}

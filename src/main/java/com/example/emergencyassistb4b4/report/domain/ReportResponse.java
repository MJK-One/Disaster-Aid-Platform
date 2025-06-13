package com.example.emergencyassistb4b4.report.domain;

import com.example.emergencyassistb4b4.global.entity.BaseEntity;
import com.example.emergencyassistb4b4.report.enums.ReportStatus;
import com.example.emergencyassistb4b4.user.domain.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@Table(name = "report_response")
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ReportResponse extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "report_response_seq_gen")
    @SequenceGenerator(
            name = "report_response_seq_gen",
            sequenceName = "report_response_seq", // DB에서 report_response_seq 시퀀스 따로 생성 필요
            allocationSize = 50
    )
    private Long id;

    //알림 재전송 할때 구분
    @Column(name = "is_notified", nullable = false)
    private boolean isNotified = false;

    @Enumerated(EnumType.STRING)
    @Column(name = "response_status", nullable = false)
    private ReportStatus status;

    private LocalDateTime notifiedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "report_id", nullable = false)
    Report report;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "responder_id", nullable = false)
    User responder;
}


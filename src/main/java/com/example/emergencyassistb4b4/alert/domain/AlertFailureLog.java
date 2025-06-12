package com.example.emergencyassistb4b4.alert.domain;

import com.example.emergencyassistb4b4.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.transaction.annotation.Transactional;

@Entity
@Table(name = "alert_failure_log")
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Transactional
public class AlertFailureLog extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "report_seq_gen")
    @SequenceGenerator(
            name = "report_seq_gen",
            sequenceName = "report_seq", // DB에 시퀀스 직접 샏성 필요
            allocationSize = 50
    )
    private Long id;

    // 어떤 신고 ID에 대한 알림이 실패했는지
    private Long reportId;

    // 실패한 알림 내용 (JSON 형태로 기록)
    @Lob
    private String alertMessage;

    // 실패 사유
    private String failureReason;
}

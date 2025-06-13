package com.example.emergencyassistb4b4.alert.domain.report;

import com.example.emergencyassistb4b4.alert.enums.DisasterType;
import com.example.emergencyassistb4b4.global.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@Entity
@Table(name = "report_alert")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@SequenceGenerator(
    name = "report_alert_seq_gen",
    sequenceName = "report_alert_seq",
    allocationSize = 50
)
public class ReportAlert extends BaseEntity {

    @Id
    @GeneratedValue(
        strategy = GenerationType.SEQUENCE,
        generator = "report_alert_seq_gen"
    )
    private Long id;

    @Column(nullable = false)
    private String si;

    @Column(nullable = false)
    private String gu;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private DisasterType disasterType;

    @Column(nullable = false)
    private Long count;

}


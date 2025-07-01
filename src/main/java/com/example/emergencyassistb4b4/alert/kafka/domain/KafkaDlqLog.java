package com.example.emergencyassistb4b4.alert.kafka.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Entity
@Builder
@Table(name = "kafka_fail_log")
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SequenceGenerator(
    name = "kafka_fail_log_seq_gen",
    sequenceName = "kafka_fail_log_seq",
    allocationSize = 50
)
public class KafkaDlqLog {

    @Id
    @GeneratedValue(
        strategy = GenerationType.SEQUENCE,
        generator = "kafka_fail_log_seq_gen"
    )
    private Long id;

    private String topic;
    private String consumerGroup;
    private String payload;

    @Column(length = 1000)
    private String reason;

    private String listener;
    private String exception;
    private LocalDateTime failedAt;

}

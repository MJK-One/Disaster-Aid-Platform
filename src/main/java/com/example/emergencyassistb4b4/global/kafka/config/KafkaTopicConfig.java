package com.example.emergencyassistb4b4.global.kafka.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KafkaTopicConfig {

    /**
     * DLQ (Dead Letter Queue) 토픽 자동 생성
     * - 이름: disaster-alert.DLT
     * - 파티션: 1
     * - 복제본: 1 (단일 브로커 환경(localhost:9092 등)일 시 1)
     */

    // DLQ 토픽
    @Bean
    public NewTopic disasterAlertDLT() {

        return new NewTopic("disaster-alert.DLT", 1, (short) 1);
    }

    // 정상 토픽
    @Bean
    public NewTopic disasterAlertTopic() {

        return new NewTopic("disaster-alert", 1, (short) 1);
    }
}

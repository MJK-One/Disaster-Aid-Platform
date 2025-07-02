package com.example.emergencyassistb4b4.report.kafka.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {

    @Bean
    public NewTopic disasterReported() {
        return TopicBuilder.name("report-reported")
            .partitions(3)
            .replicas(1)
            .build();
    }

    @Bean
    public NewTopic disasterReportedDLT() {
        return TopicBuilder.name("report-reported-dlt")
            .partitions(3)
            .replicas(1)
            .build();
    }
}

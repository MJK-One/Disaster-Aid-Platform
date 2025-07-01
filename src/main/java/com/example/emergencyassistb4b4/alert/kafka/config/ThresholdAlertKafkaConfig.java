package com.example.emergencyassistb4b4.alert.kafka.config;

import com.example.emergencyassistb4b4.global.kafka.dto.DisasterReportedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;

@Configuration
@RequiredArgsConstructor
public class ThresholdAlertKafkaConfig {

    private final KafkaBaseConfig kafkaBaseConfig;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Bean
    public ConsumerFactory<String, DisasterReportedEvent> thresholdConsumerFactory() {
        return new DefaultKafkaConsumerFactory<>(
            kafkaBaseConfig.baseConsumerProps("alert-threshold-group", DisasterReportedEvent.class.getName())
        );
    }

    @Bean(name = "thresholdListenerFactory")
    public ConcurrentKafkaListenerContainerFactory<String, DisasterReportedEvent> thresholdListenerFactory() {
        var factory = new ConcurrentKafkaListenerContainerFactory<String, DisasterReportedEvent>();
        factory.setConsumerFactory(thresholdConsumerFactory());
        factory.setConcurrency(3);
        factory.setCommonErrorHandler(kafkaBaseConfig.defaultErrorHandler(kafkaTemplate));
        return factory;
    }
}



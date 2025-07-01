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
public class ImmediateAlertKafkaConfig {

    private final KafkaBaseConfig kafkaBaseConfig;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Bean
    public ConsumerFactory<String, DisasterReportedEvent> immediateConsumerFactory() {
        return new DefaultKafkaConsumerFactory<>(
            kafkaBaseConfig.baseConsumerProps("alert-immediate-group", DisasterReportedEvent.class.getName())
        );
    }

    @Bean(name = "immediateListenerFactory")
    public ConcurrentKafkaListenerContainerFactory<String, DisasterReportedEvent> immediateListenerFactory() {
        var factory = new ConcurrentKafkaListenerContainerFactory<String, DisasterReportedEvent>();
        factory.setConsumerFactory(immediateConsumerFactory());
        factory.setConcurrency(3);
        factory.setCommonErrorHandler(kafkaBaseConfig.defaultErrorHandler(kafkaTemplate));
        return factory;
    }
}

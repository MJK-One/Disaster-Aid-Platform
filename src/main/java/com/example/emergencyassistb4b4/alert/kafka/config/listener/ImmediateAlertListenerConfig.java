package com.example.emergencyassistb4b4.alert.kafka.config.listener;

import com.example.emergencyassistb4b4.alert.kafka.config.base.KafkaBaseConfig;
import com.example.emergencyassistb4b4.global.kafka.dto.DisasterReportedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.ContainerProperties;

@Configuration
@RequiredArgsConstructor
public class ImmediateAlertListenerConfig {

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
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.RECORD);
        factory.setCommonErrorHandler(kafkaBaseConfig.defaultErrorHandler(kafkaTemplate));
        return factory;
    }
}

package com.example.emergencyassistb4b4.global.kafka.config;

import com.example.emergencyassistb4b4.global.kafka.dto.DisasterAlertMessage;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.util.backoff.FixedBackOff;

import java.util.HashMap;
import java.util.Map;
import org.apache.kafka.common.serialization.StringDeserializer;

@EnableKafka // Kafka 리스너 활성화
@Configuration
public class KafkaListenerConfig { // Consumer 전용 설정(ListenerFactory, DLQ, Retry 등)

    @Bean
    public ConsumerFactory<String, DisasterAlertMessage> consumerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(org.apache.kafka.clients.consumer.ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(org.apache.kafka.clients.consumer.ConsumerConfig.GROUP_ID_CONFIG, "disaster-alert-consumer-group");
        props.put(org.apache.kafka.clients.consumer.ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(org.apache.kafka.clients.consumer.ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);
        props.put(ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS, JsonDeserializer.class);
        props.put(JsonDeserializer.TRUSTED_PACKAGES, "*");
        props.put(JsonDeserializer.VALUE_DEFAULT_TYPE, "com.example.emergencyassistb4b4.global.kafka.dto.DisasterAlertMessage"); // DTO 풀패키지명

        return new DefaultKafkaConsumerFactory<>(props);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, DisasterAlertMessage> kafkaListenerContainerFactory( // 리스너 팩토리 직접 설정
            ConsumerFactory<String, DisasterAlertMessage> consumerFactory,
            KafkaTemplate<String, Object> kafkaTemplate // 주입 받아서 사용
    ) {
        ConcurrentKafkaListenerContainerFactory<String, DisasterAlertMessage> factory = new ConcurrentKafkaListenerContainerFactory<>();

        factory.setConsumerFactory(consumerFactory);

        // 레코드 단위로 ACK 처리 -> DLQ 전송 보장
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.RECORD);

        // Retry 설정 -> 최대 3번 시도 후 DLQ로 보냄
        factory.setCommonErrorHandler(new DefaultErrorHandler(new DeadLetterPublishingRecoverer(kafkaTemplate), new FixedBackOff(1000L, 3))); // 1초 간격으로 최대 3회 재시도 후 DLQ

        return factory;
    }

    // 1초 간격 3번까지 재시도에도 실패 시 기본 규칙대로 원래 토픽명 + ".DLT" 토픽에 전송 (ex. disaster-alert -> 실패 -> disaster-alert.DLT로 전송됨)
    @Bean
    public DeadLetterPublishingRecoverer deadLetterPublishingRecoverer(KafkaTemplate<String, Object> kafkaTemplate) {

        return new DeadLetterPublishingRecoverer(kafkaTemplate); // Recoverer가 DLQ로 전송 가능하게 함
    }
}

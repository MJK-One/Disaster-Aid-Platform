package com.example.emergencyassistb4b4.global.kafka.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.util.backoff.FixedBackOff;

import java.util.HashMap;
import java.util.Map;

@EnableKafka // Kafka 리스너 활성화
@Configuration
public class KafkaListenerConfig { // Consumer 전용 설정(ListenerFactory, DLQ, Retry 등)

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, String> kafkaListenerContainerFactory( // 리스너 팩토리 직접 설정
            ConsumerFactory<String, String> consumerFactory,
            KafkaTemplate<String, String> kafkaTemplate // 주입 받아서 사용
    ) {
        ConcurrentKafkaListenerContainerFactory<String, String> factory = new ConcurrentKafkaListenerContainerFactory<>();

        factory.setConsumerFactory(consumerFactory);

        // Retry 설정 -> 최대 3번 시도 후 DLQ로 보냄
        factory.setCommonErrorHandler(new DefaultErrorHandler(new DeadLetterPublishingRecoverer(kafkaTemplate), new FixedBackOff(1000L, 3))); // 1초 간격으로 최대 3회 재시도 후 DLQ

        return factory;
    }

    // 1초 간격 3번까지 재시도에도 실패 시 기본 규칙대로 원래 토픽명 + ".DLT" 토픽에 전송 (ex. disaster-alert -> 실패 -> disaster-alert.DLT로 전송됨)
    @Bean
    public DeadLetterPublishingRecoverer deadLetterPublishingRecoverer(KafkaTemplate<String, String> kafkaTemplate) {

        return new DeadLetterPublishingRecoverer(kafkaTemplate); // Recoverer가 DLQ로 전송 가능하게 함
    }
}

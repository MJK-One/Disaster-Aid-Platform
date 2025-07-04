package com.example.emergencyassistb4b4.global.kafka.config;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

import java.util.HashMap;
import java.util.Map;
import org.springframework.kafka.support.serializer.JsonSerializer;

@Configuration
@Profile("!test") // 운영 환경에서만 활성화
public class KafkaConfig { // Producer 전용 설정(ProducerFactory, KafkaTemplate, ObjectMapper) >> 공통 Bean은 KafkaConfig에서 등록하고 ConsumerConfig에서 주입받아 사용

    // Kafka ProducerFactory 설정 (KafkaTemplate에서 사용됨)
    @Bean
    public ProducerFactory<String, Object> producerFactory() {

        Map<String, Object> config = new HashMap<>();

        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092"); // kafka 서버 주소
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);

        return new DefaultKafkaProducerFactory<>(config);
    }

    // kafka 메시지 발행 시 사용
    @Bean
    public KafkaTemplate<String, Object> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }
}

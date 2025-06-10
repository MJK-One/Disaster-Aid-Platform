package com.example.emergencyassistb4b4;

import com.example.emergencyassistb4b4.global.kafka.dto.DisasterAlertMessage;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;

@ActiveProfiles("test")
@SpringBootTest(properties = "spring.profiles.active=test")
@EmbeddedKafka(partitions = 1, topics = { "disaster-alert" }, brokerProperties = { "listeners=PLAINTEXT://localhost:0" })
class DisasterAlertKafkaTest {

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testKafkaSend() throws Exception {

        // 테스트용 메시지 생성
        DisasterAlertMessage message = DisasterAlertMessage.builder()
                .reportId(123L)
                .disasterType("EARTHQUAKE")
                .location("서울 강남구")
                .reporterId(1L)
                .reportedAt(LocalDateTime.now())
                .build();

        // JSON 직렬화
        String json = objectMapper.writeValueAsString(message);

        // Kafka 발행
        kafkaTemplate.send("disaster-alert", json);

        // 결과 확인용 (일단은 수동 확인 or Consumer에서 로그 확인 가능)
        System.out.println("✅ Kafka 테스트 메시지 발행 완료: " + json);

        // Optional → Consumer 쪽 테스트는 ConsumerRecordListener 활용 가능 (고급 예시 가능)
    }
}



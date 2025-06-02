package com.example.emergencyassistb4b4;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class EmergencyAssistB4B4Application {

    public static void main(String[] args) {
        SpringApplication.run(EmergencyAssistB4B4Application.class, args);
    }

}

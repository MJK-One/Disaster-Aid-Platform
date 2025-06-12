package com.example.emergencyassistb4b4.userDevice.domain;

import com.example.emergencyassistb4b4.global.entity.BaseEntity;
import com.example.emergencyassistb4b4.user.domain.User;
import com.example.emergencyassistb4b4.userDevice.enums.DeviceOs;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserDevice extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id",  nullable = false)
    private User user;

    @Column(nullable = false, unique = true)
    private String deviceId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DeviceOs os;

    @Column(nullable = false)
    private String fcmToken;

    public void updateToken(String token) {
        this.fcmToken = token;
    }
}


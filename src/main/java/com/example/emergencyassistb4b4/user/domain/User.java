package com.example.emergencyassistb4b4.user.domain;

import com.example.emergencyassistb4b4.auth.oauth.dto.SocialUserUpdateDto;
import com.example.emergencyassistb4b4.global.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@Entity
@AllArgsConstructor
@Table(name = "users")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nickname", unique = true)
    private String nickname;

    @Column(length = 20)
    private String phoneNumber;

    @Column(unique = true, nullable = false, length = 100)
    private String email; //필수

    @Column(length = 255)
    private String password; //필수

    @Enumerated(EnumType.STRING)
    @Column(name = "login_type")
    private LoginType loginType; //필수

    @Column(name = "provider", length = 255)
    private String provider;

    private String businessNumber;
    private String organizationName;

    @Enumerated(EnumType.STRING)
    @Column(name = "user_role")
    private UserRole userRole;

    private LocalDateTime lastLoginAt;


    @Builder
    public User(String email, String password, String nickname, LoginType loginType, String provider, UserRole userRole) {
        this.email = email;
        this.password = password;
        this.nickname = nickname;
        this.loginType = loginType;
        this.provider = provider;
        this.userRole = userRole;
        this.lastLoginAt = LocalDateTime.now();
    }


    public User updateNickname(String nickname) {
        this.nickname = nickname;
        return this;
    }

    public User updateSocialInfo(SocialUserUpdateDto dto) {
        this.nickname = dto.getNickname();
        return this;
    }
}

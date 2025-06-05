package com.example.emergencyassistb4b4.user.domain;

import com.example.emergencyassistb4b4.global.entity.BaseEntity;
import com.example.emergencyassistb4b4.user.domain.enums.LoginType;
import com.example.emergencyassistb4b4.user.domain.enums.UserRole;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

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

    @Column(nullable = false, length = 255)
    private String password; //필수

    @Enumerated(EnumType.STRING)
    @Column(name = "login_type", nullable = false)
    private LoginType loginType; //필수

    @Column(name = "provider", length = 255)
    private String provider;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
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
}

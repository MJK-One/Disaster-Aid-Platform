package com.example.emergencyassistb4b4.global.security;

import com.example.emergencyassistb4b4.auth.jwt.JwtProperties;
import com.example.emergencyassistb4b4.auth.jwt.JwtTokenProvider;
import com.example.emergencyassistb4b4.user.domain.LoginType;
import com.example.emergencyassistb4b4.user.domain.User;
import com.example.emergencyassistb4b4.user.domain.UserRole;
import com.example.emergencyassistb4b4.user.repository.UserRepository;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.security.Key;
import java.time.Duration;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
public class TokenProviderTest {
    @Autowired private JwtTokenProvider jwtTokenProvider;
    @Autowired private JwtProperties jwtProperties;
    @Autowired private UserRepository userRepository;


    @Test
    @DisplayName("generateToken() : 유저정보와 만료기간을 전달해 토큰을 만들 수 있다.")
    public void generateToken() {
        // given
        User testUser = userRepository.save(User.builder()
                .email("user@gmail.com")
                .password("test")
                        .loginType(LoginType.LOCAL)
                        .userRole(UserRole.USER)
                .build());
        // when
        String token = jwtTokenProvider.generateToken(testUser, Duration.ofDays(14));
        // then
        Key key = Keys.hmacShaKeyFor(jwtProperties.getSecret().getBytes());
        Long userId = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .get("id", Long.class);
        assertThat(userId).isEqualTo(testUser.getId());
    }
}

package com.example.emergencyassistb4b4.auth.jwt;

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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;

import java.security.Key;
import java.time.Duration;
import java.util.Date;
import java.util.Map;

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
    @DisplayName("validToken(): 만료된 토큰일 때에 유효성 검증에 실패한다.")
    @Test
    public void validToken_invalidToken()  {
        // given
        String token = JwtFactory.builder()
                .expiration(new Date(new Date().getTime() - Duration.ofDays(7).toMillis()))
                .build().createToken(jwtProperties);

        // when
        boolean result = jwtTokenProvider.validateToken(token);
        // then
        assertThat(result).isFalse();
    }

    @DisplayName("validToken() : 유효한 토큰인 때에 유효성 검증에 성공")
    @Test
    public void validToken_validToken()  {
        // given
        String token = JwtFactory.withDefaultValues().createToken(jwtProperties);
        // when
        boolean result = jwtTokenProvider.validateToken(token);
        // then
        assertThat(result).isTrue();
    }
    @DisplayName("getAuthentication() : 토큰 기반으로 인증 정보를 가져올 수 없다")
    @Test
    public void getAuthentication() throws Exception {
        // given
        String userEmail = "user@gmail.com";
        String token = JwtFactory.builder()
                .subject(userEmail)
                .build().createToken(jwtProperties);
        // when
        Authentication authentication = jwtTokenProvider.getAuthentication(token);
        // then
        assertThat(((UserDetails) authentication.getPrincipal()).getUsername()).isEqualTo(userEmail);
    }

    @DisplayName("getUserId() : 토큰으로 유저아이디를 가져올 수 있다.")
    @Test
    public void getUserId() throws Exception {
        // given
          Long userId = 1L;
          String token = JwtFactory.builder()
                  .claims(Map.of("id",userId))
                  .build().createToken(jwtProperties);
        // when
        Long userIdByToken = jwtTokenProvider.getUserId(token);
        // then
        assertThat(userIdByToken).isEqualTo(userId);

    }
}

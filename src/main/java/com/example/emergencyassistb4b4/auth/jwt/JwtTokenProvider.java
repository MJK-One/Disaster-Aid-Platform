package com.example.emergencyassistb4b4.auth.jwt;

import com.example.emergencyassistb4b4.user.domain.User;
import com.example.emergencyassistb4b4.user.dto.UserResponse;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Header;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.Duration;
import java.util.Collections;
import java.util.Date;
import java.util.Set;

@RequiredArgsConstructor
@Service
@Log4j2
public class JwtTokenProvider {
    // 토큰 생성, 파싱, 인증 변환 등을 처리하는 핵심 서비스
    private final JwtProperties jwtProperties; //jwt 비밀키 등의 값을 주입받기 위한 객체

    /**
     * 사용자 정보를 기반으로 JWT 토큰 생성
     * @param user 토큰에 포함될 사용자 정보
     * @param expiredAt  토큰의 만료 기간
     * @return  JWT 문자열
     */
    public String generateToken(UserResponse user, Duration expiredAt) {
        Date now = new Date(); // 현재 시간 기준으로 Date 객체 생성

        return makeToken(new Date(now.getTime() + expiredAt.toMillis()), user); // expiredAt 만큼 더한 만료시간 설정
    }
    /**
     * 만료 시간과 사용자 정보를 기반으로 JWT를 생성
     * @param expiry 토큰 만료 시각
     * @param user 사용자 정보
     * @return JWT 문자열
     */
    public String makeToken(Date expiry, UserResponse user) {
        Date now = new Date();
        return Jwts.builder()
                .setHeaderParam(Header.TYPE, Header.JWT_TYPE) // 헤더 typ : JWT
                .setIssuer(jwtProperties.getIssuer()) // 내용 iss : yml 파일에서 설정한 값
                .setIssuedAt(now) // 내용 iat : 현재 시간
                .setExpiration(expiry) // 내용 exp : expiry 멤버 변숫값
                .setSubject(user.getEmail()) // 내용 sub : 유저의 이메일
                .claim("id", user.getId()) // 클레임 id : 유저 ID
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                // 서명 : 비밀값과 함께 해시값을 ~ 방식으로 암호화
                .compact();
    }
    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8));
    }
    /**
     * JWT 토큰 유효성 검증 메서드
     * yml 파일에 선언한 비밀값과 함께 토큰 복호화를 진행 후 아무 에러도 발생하지 않으면 true 반환
     * @param  token 토큰
     * @return boolean 값
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token).getBody();
            return true;
        } catch (Exception e) {
            // 디버깅 용, 추후 삭제 예정
            log.warn("Invalid JWT token: {}", e.getMessage());
            return false;
        }
    }

    /**
     * 토큰 기반으로 인증 정보를 가져오는 메서드
     *
     * @param
     * @return Authentication 토큰을 받아 인증 정보를 담은 객체 Authentication 을 반환
     */
    public Authentication getAuthentication(String token) {
        Claims claims = getClaims(token); // jwt에서 claims(사용자 정보 등)를 추출

        // 권한 정보 생성 ( 기본값은 ROLE_USER )
        String role = claims.get("role", String.class);
        Set<SimpleGrantedAuthority> authorities = Collections.singleton(new SimpleGrantedAuthority("ROLE_" + role));
        // 사용자 정보를 기반으로 Spring Security 용 User 객체 생성
        return new UsernamePasswordAuthenticationToken(
                new org.springframework.security.core.userdetails.User(
                        claims.getSubject(),"", authorities), token,authorities);
        // subject 는 주로 이메일이나 userId로 설정, 비밀번호는 없음 (인증용 객체라 ) , 위에서 생성한 권한 정보
        // token :  credentials (여기서는 JWT 토큰 자체)
        // authorities : 권한 정보 다시 주입

    }
    public Long getUserId(String token) { // JWT에서 사용자 ID를 추출하는 메서드
        Claims claims = getClaims(token); // 클레임 정보 추출
        return claims.get("id", Long.class); // "id" 키를 Long 타입으로 가져옴
    }

    // JWT에서 Claims(페이로드 부분)를 파싱해 가져오는 내부 유틸 메서드
    private Claims getClaims(String token) {
        return Jwts.parserBuilder() //  JWT 파서 생성
                .setSigningKey(getSigningKey()) // 시크릿 키 설정
                .build()
                .parseClaimsJws(token) // JWT 문자열을 파싱
                .getBody(); // 클레임(body) 반환

    }
}

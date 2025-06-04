package com.example.emergencyassistb4b4.auth.security;

import com.example.emergencyassistb4b4.auth.jwt.JwtTokenAuthenticationFilter;
import com.example.emergencyassistb4b4.auth.jwt.JwtTokenProvider;
import com.example.emergencyassistb4b4.auth.oauth.handler.OAuth2SuccessHandler;
import com.example.emergencyassistb4b4.auth.oauth.repository.OAuth2AuthorizationRequestBasedOnCookieRepository;
import com.example.emergencyassistb4b4.auth.oauth.service.Oauth2UserCustomService;
import com.example.emergencyassistb4b4.auth.redis.RefreshTokenRepository;
import com.example.emergencyassistb4b4.user.service.UserReadService;
import com.example.emergencyassistb4b4.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.util.AntPathMatcher;

@RequiredArgsConstructor
@Configuration
public class WebOAuth2SecurityConfig {
    private final Oauth2UserCustomService oauth2UserCustomService;
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserReadService userReadService;
  /*  // 정적 리소스 무시 설정
    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring()
                .requestMatchers( "/static/**");
    }*/

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/static/**").permitAll()
                        .requestMatchers(
                                "/api/auth/signup",
                                "/api/auth/login",
                                "/api/refresh",
                                "/static/**"
                        ).permitAll()
                        .requestMatchers("/api/**").authenticated()
                        .anyRequest().authenticated()
                )
                .csrf(AbstractHttpConfigurer::disable) // CSRF 보호 비활성화 (JWT 기반에선 불필요)
                .httpBasic(AbstractHttpConfigurer::disable) // HTTP BASIC 비활성화
                .formLogin(AbstractHttpConfigurer::disable) // 기본 폼 사용 X
                .logout(AbstractHttpConfigurer::disable) // 로그아웃 비활성화
                .sessionManagement(management -> management.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) //세션 사용안함
                .addFilterBefore(new JwtTokenAuthenticationFilter(jwtTokenProvider), UsernamePasswordAuthenticationFilter.class) //JWT 필터 등록

                .oauth2Login(oauth2 -> oauth2.loginPage("/login") //oauth 로그인 페이지 경로
                        .authorizationEndpoint(endpoint -> endpoint
                                .baseUri("/oauth2/authorize")
                                // 쿠키 기반 저장소 사용
                                .authorizationRequestRepository(oAuth2AuthorizationRequestBasedOnCookieRepository()))
                       .userInfoEndpoint(endpoint -> endpoint
                                .userService(oauth2UserCustomService))) // 로그인 이후 사용자 정보 처리 커스텀 서비스
                .build();
    }

    // 성공 처리 핸들러 ( 토큰 발급 + 저장 )
    @Bean
    public OAuth2SuccessHandler oAuth2SuccessHandler() {
        return new OAuth2SuccessHandler(jwtTokenProvider,
                refreshTokenRepository,
                oAuth2AuthorizationRequestBasedOnCookieRepository(),
                userReadService
        );
    }

    @Bean
    public JwtTokenAuthenticationFilter jwtTokenAuthenticationFilter() {
        return new JwtTokenAuthenticationFilter(jwtTokenProvider);
    }

    @Bean
    public OAuth2AuthorizationRequestBasedOnCookieRepository oAuth2AuthorizationRequestBasedOnCookieRepository() {
        return new OAuth2AuthorizationRequestBasedOnCookieRepository();
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}

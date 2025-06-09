package com.example.emergencyassistb4b4.global.config;

import com.example.emergencyassistb4b4.auth.oauth.handler.OAuth2SuccessHandler;
import com.example.emergencyassistb4b4.auth.oauth.repository.OAuth2AuthorizationRequestBasedOnCookieRepository;
import com.example.emergencyassistb4b4.auth.oauth.service.OAuth2UserCustomService;
import com.example.emergencyassistb4b4.auth.token.RefreshTokenService;
import com.example.emergencyassistb4b4.global.security.JwtTokenAuthenticationFilter;
import com.example.emergencyassistb4b4.global.security.JwtUtils;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@RequiredArgsConstructor
@Configuration
public class WebOAuth2SecurityConfig {

    private final JwtUtils jwtUtils;
    private final OAuth2UserCustomService oAuth2UserCustomService;
    private final RefreshTokenService refreshTokenService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/static/**",
                                "/api/auth/signup",
                                "/api/auth/login",
                                "/oauth2/**",
                                "/api/refresh"

                        ).permitAll()
                        .requestMatchers("/api/**").authenticated()
                        .anyRequest().authenticated()
                )
                .csrf(AbstractHttpConfigurer::disable) // CSRF 보호 비활성화 (JWT 기반에선 불필요)
                .httpBasic(AbstractHttpConfigurer::disable) // HTTP BASIC 비활성화
                .formLogin(AbstractHttpConfigurer::disable) // 기본 폼 사용 X
                .logout(AbstractHttpConfigurer::disable) // 로그아웃 비활성화
                .sessionManagement(management -> management.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) //세션 사용안함
                .addFilterBefore(jwtTokenAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class) //JWT 필터 등록
                .oauth2Login(oauth2 -> oauth2
                        .successHandler(oAuth2SuccessHandler())
                        .authorizationEndpoint(endpoint -> endpoint
                                .baseUri("/oauth2/authorization")
                                // 쿠키 기반 저장소 사용
                                .authorizationRequestRepository(oAuth2AuthorizationRequestBasedOnCookieRepository()))
                        .userInfoEndpoint(endpoint -> endpoint
                                .userService(oAuth2UserCustomService)))// 로그인 이후 사용자 정보 처리 커스텀 서비스


                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint((request, response, authException) -> {
                            response.setStatus(HttpServletResponse.SC_ACCEPTED);
                            response.setContentType("application/json");
                            response.getWriter().write("Unauthorized");
                        }))
                .build();
    }

    @Bean
    public OAuth2SuccessHandler oAuth2SuccessHandler() {
        return new OAuth2SuccessHandler(jwtUtils,
                refreshTokenService,
                oAuth2AuthorizationRequestBasedOnCookieRepository()
        );
    }


    @Bean
    public OAuth2AuthorizationRequestBasedOnCookieRepository oAuth2AuthorizationRequestBasedOnCookieRepository() {
        return new OAuth2AuthorizationRequestBasedOnCookieRepository();
    }
    @Bean
    public JwtTokenAuthenticationFilter jwtTokenAuthenticationFilter() {
        return new JwtTokenAuthenticationFilter(jwtUtils);
    }
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}

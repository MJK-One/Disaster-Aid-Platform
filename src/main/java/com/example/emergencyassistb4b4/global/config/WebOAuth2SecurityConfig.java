package com.example.emergencyassistb4b4.global.config;

import com.example.emergencyassistb4b4.auth.oauth.handler.OAuth2SuccessHandler;
import com.example.emergencyassistb4b4.auth.oauth.repository.OAuth2AuthorizationRequestBasedOnCookieRepository;
import com.example.emergencyassistb4b4.auth.oauth.service.KakaoService;
import com.example.emergencyassistb4b4.auth.oauth.service.OAuth2UserCustomService;
import com.example.emergencyassistb4b4.auth.token.TokenService;
import com.example.emergencyassistb4b4.global.security.JwtTokenAuthenticationFilter;
import com.example.emergencyassistb4b4.global.security.JwtUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AnonymousAuthenticationFilter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Map;

@RequiredArgsConstructor
@Configuration
@EnableMethodSecurity(prePostEnabled = true)
@Slf4j
public class WebOAuth2SecurityConfig {

    private final JwtUtils jwtUtils;
    private final OAuth2UserCustomService oAuth2UserCustomService;
    private final JwtTokenAuthenticationFilter jwtTokenAuthenticationFilter;

    @Bean
    @Order(SecurityProperties.BASIC_AUTH_ORDER)
    public SecurityFilterChain securityFilterChain(HttpSecurity http, TokenService tokenService, KakaoService kakaoService) throws Exception {
         http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/static/**",
                                "/auth/signup",
                                "/auth/login",
                                "/login/oauth2/code/kakao",
                                "/oauth2/authorization/kakao",
                                "/oauth2/**",
                                "/login/oauth2/code/**",
                                "/auth/reissue",
                                "/error",
                                "/tracking",
                                "/location-tracking" // WebSocket 핸드쉐이크 경로 허용 추가
                                "/login/oauth2/code/**"

                        ).permitAll()
                        //.requestMatchers("/api/**").authenticated()
                        .anyRequest().authenticated()
                )
                .csrf(AbstractHttpConfigurer::disable) // CSRF 보호 비활성화 (JWT 기반에선 불필요)
                .httpBasic(AbstractHttpConfigurer::disable) // HTTP BASIC 비활성화
                .formLogin(AbstractHttpConfigurer::disable) // 기본 폼 사용 X
                .logout(AbstractHttpConfigurer::disable) // 로그아웃 비활성화
                .sessionManagement(management -> management.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // 세션 사용 안함
                // 여기서 필터 순서를 AnonymousAuthenticationFilter 이전으로 변경
                .addFilterBefore(jwtTokenAuthenticationFilter, AnonymousAuthenticationFilter.class)
                .oauth2Login(oauth2 -> oauth2
                        .successHandler(oAuth2SuccessHandler(tokenService))
                        //oauth 인증 성공 후 사용자 정보를 가져오고 성공 핸들러를 통해 jwt 토큰 발급
                        .successHandler(oAuth2SuccessHandler(tokenService, kakaoService))

                        .authorizationEndpoint(endpoint -> endpoint
                                .baseUri("/oauth2/authorization")
                                .authorizationRequestRepository(new OAuth2AuthorizationRequestBasedOnCookieRepository()))
                        .redirectionEndpoint(endpoint -> endpoint
                                .baseUri("/login/oauth2/code/*"))
                        .userInfoEndpoint(endpoint -> endpoint
                                .userService(oAuth2UserCustomService))
                )
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint((request, response, authException) -> {
                            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                            response.setContentType("application/json; charset=utf-8");
                            response.getWriter().write(
                                    new ObjectMapper().writeValueAsString(Map.of("error", "Unauthorized"))
                            );
                        })
                );
        return http.build();
    }

    @Bean
    public OAuth2SuccessHandler oAuth2SuccessHandler(TokenService tokenService, KakaoService kakaoService) {

        return new OAuth2SuccessHandler(
                tokenService,
                kakaoService,
                new OAuth2AuthorizationRequestBasedOnCookieRepository()
        );
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        return http.getSharedObject(AuthenticationManagerBuilder.class).build();
    }

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedOrigins("http://localhost:3000", "http://127.0.0.1:5501")
                        .allowedMethods("*")
                        .allowedHeaders("*")
                        .allowCredentials(true);
            }
        };
    }
}

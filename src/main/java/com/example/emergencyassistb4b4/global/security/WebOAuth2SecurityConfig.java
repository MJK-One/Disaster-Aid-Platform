package com.example.emergencyassistb4b4.global.security;

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

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/static/**").permitAll()
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
                .addFilterBefore(new JwtTokenAuthenticationFilter(jwtUtils), UsernamePasswordAuthenticationFilter.class) //JWT 필터 등록


                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint((request, response, authException) -> {
                            response.setStatus(HttpServletResponse.SC_ACCEPTED);
                            response.getWriter().write("Unauthorized");
                        }))
                .build();
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

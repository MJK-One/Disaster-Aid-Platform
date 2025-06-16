package com.example.emergencyassistb4b4.global.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

// 1. /api/auth, /oauth2 등 경로는 필터 제외
// 2. Authorization 헤더의 Bearer 토큰 추출
// 3. JWT 유효성 검증 → 성공 시 SecurityContext에 Authentication 설정
@RequiredArgsConstructor
@Slf4j
@Component
public class JwtTokenAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtils jwtUtils;


    public final static String HEADER_AUTHORIZATION = "Authorization";
    public final static String HEADER_PREFIX = "Bearer ";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String path = request.getRequestURI();
        // 필터 예외 경로 처리
        if(isSkipPath(path)) {
            filterChain.doFilter(request, response);
            return;
        }

        // 요청 헤더의 Authorization 키의 값 조회
        String authorizationHeader = request.getHeader(HEADER_AUTHORIZATION);

        //가져온 값에서 접두사 제거
        String token = getAccessToken(authorizationHeader);

        //가져온 토큰이 유효한지 확인하고 유효한 때는 인증 정보 설정
        if (token != null) {
            if (jwtUtils.validateToken(token)) {
                Authentication authentication = jwtUtils.getAuthentication(token);
                SecurityContext context = SecurityContextHolder.createEmptyContext();
                context.setAuthentication(authentication);
                SecurityContextHolder.setContext(context);


            } else {
                log.warn("Invalid JWT token.");
            }

        }
        filterChain.doFilter(request, response);
    }
    private String getAccessToken(String authorizationHeader) {
        if (authorizationHeader != null && authorizationHeader.startsWith(HEADER_PREFIX)) {
            return authorizationHeader.substring(HEADER_PREFIX.length());
        }
        return null;
    }
    // 추후에 경로가 많아질 것을 생각해서 Set<String>, PathMatcher 활용한 구성으로 리팩토링 예정
    private boolean isSkipPath(String path) {
        return path.matches("^/api/login/oauth2/code/.*") ||
                path.startsWith("/api/auth") ||
                path.startsWith("/oauth2");
    }
}
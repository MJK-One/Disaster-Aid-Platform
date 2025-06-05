package com.example.emergencyassistb4b4.global.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@RequiredArgsConstructor
@Log4j2
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
        if (token != null && jwtUtils.validateToken(token)) {
            Authentication authentication = jwtUtils.getAuthentication(token);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        } else {
            log.info("No JWT token in header : 토큰이 없음!!!! ");
        }
        filterChain.doFilter(request, response);
    }
    private String getAccessToken(String authorizationHeader) {
        if (authorizationHeader != null && authorizationHeader.startsWith(HEADER_PREFIX)) {
            return authorizationHeader.substring(HEADER_PREFIX.length());

        }
        return null;
    }
    private boolean isSkipPath(String path) {
        return path.startsWith("/api/auth/signup")
                || path.startsWith("/api/auth/login")
                || path.startsWith("/oauth2")
                || path.startsWith("/api/auth/refresh");
    }
}
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
        log.debug("Incoming request URL: {}", path);

        // 필터 예외 경로 처리
        if (isSkipPath(path)) {
            log.debug("Skipping JWT filter for path: {}", path);
            filterChain.doFilter(request, response);
            return;
        }

        String token = resolveToken(request);
        if (token != null) {
            // 토큰 일부만 마스킹해서 출력 (앞 6글자 + 뒤 6글자)
            String maskedToken = token.length() > 12 ? token.substring(0, 6) + "..." + token.substring(token.length() - 6) : token;
            log.debug("JWT token found: {}", maskedToken);

            if (jwtUtils.validateToken(token)) {
                Authentication authentication = jwtUtils.getAuthentication(token);
                SecurityContext context = SecurityContextHolder.createEmptyContext();
                context.setAuthentication(authentication);
                SecurityContextHolder.setContext(context);
                log.debug("Authentication set for user: {}", authentication.getName());
            } else {
                log.warn("Invalid JWT token.");
            }
        } else {
            log.debug("No JWT token found in request.");
        }

        filterChain.doFilter(request, response);
    }
    private String resolveToken(HttpServletRequest request) {
        // 1. Authorization 헤더 우선
        String authorizationHeader = request.getHeader(HEADER_AUTHORIZATION);
        if (authorizationHeader != null && authorizationHeader.startsWith(HEADER_PREFIX)) {
            return authorizationHeader.substring(HEADER_PREFIX.length());
        }

        // 2. fallback: 쿼리 파라미터 token
        return request.getParameter("token");
    }

    // 추후에 경로가 많아질 것을 생각해서 Set<String>, PathMatcher 활용한 구성으로 리팩토링 예정
    private boolean isSkipPath(String path) {
        return path.matches("^/api/login/oauth2/code/.*") ||
                path.startsWith("/api/auth") ||
                path.startsWith("/oauth2") ||
                path.equals("/error");  // 추가
    }
}
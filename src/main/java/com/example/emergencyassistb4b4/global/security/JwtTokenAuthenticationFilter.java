package com.example.emergencyassistb4b4.global.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Set;

@RequiredArgsConstructor
@Slf4j
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
        log.debug("Authorization Header: {}", authorizationHeader);
        //가져온 값에서 접두사 제거
        String token = getAccessToken(authorizationHeader);

        //가져온 토큰이 유효한지 확인하고 유효한 때는 인증 정보 설정
        if (token != null) {
            if (jwtUtils.validateToken(token)) {
                Authentication authentication = jwtUtils.getAuthentication(token);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } else {
                log.warn("Invalid JWT token.");
            }
        }
        log.debug("Token: {}", token);
        log.debug("Authentication: {}", SecurityContextHolder.getContext().getAuthentication());

        filterChain.doFilter(request, response);
    }
    private String getAccessToken(String authorizationHeader) {
        if (authorizationHeader != null && authorizationHeader.startsWith(HEADER_PREFIX)) {
            return authorizationHeader.substring(HEADER_PREFIX.length());

        }
        return null;
    }
    // 추후에 경로가 많아질 것을 생각해서 Set<String>, PathMatcher 활용한 구성으로 리팩토링 예정
    private static final Set<String> SKIP_PATHS = Set.of(
            "/api/auth/signup",
            "/api/auth/login",
            "/oauth2",
            "/api/auth/reissue",
            "/api/login/oauth2/code"
    );

    private boolean isSkipPath(String path) {
        return path.matches("^/api/login/oauth2/code/.*") ||
                path.startsWith("/api/auth") ||
                path.startsWith("/oauth2");
    }
}
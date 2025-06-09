package com.example.emergencyassistb4b4.auth.oauth.handler;

import com.example.emergencyassistb4b4.auth.dto.TokenResponseDto;
import com.example.emergencyassistb4b4.auth.oauth.repository.OAuth2AuthorizationRequestBasedOnCookieRepository;
import com.example.emergencyassistb4b4.auth.token.RefreshTokenService;
import com.example.emergencyassistb4b4.auth.token.TokenService;
import com.example.emergencyassistb4b4.global.security.JwtUtils;
import com.example.emergencyassistb4b4.global.util.CookieUtil;
import com.example.emergencyassistb4b4.user.domain.User;
import com.example.emergencyassistb4b4.user.dto.UserResponseDto;
import com.example.emergencyassistb4b4.user.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.time.Duration;
import java.util.Map;

@RequiredArgsConstructor
public class OAuth2SuccessHandler  extends SimpleUrlAuthenticationSuccessHandler {
    public static final String REFRESH_TOKEN = "refresh_token";
    public static final Duration REFRESH_TOKEN_DURATION = Duration.ofDays(14);
    public static final Duration ACCESS_TOKEN_DURATION = Duration.ofHours(1);

    // 하드코딩 된 부분 수정 예정
    public static final String REDIRECT_URI = "http://localhost:5173/oauth2/redirect";

    private final TokenService tokenService;

    private final OAuth2AuthorizationRequestBasedOnCookieRepository oauth2AuthorizationRequestBasedOnCookieRepository;



    /**
     * OAuth2 인증 성공 시 호출되는 메서드
     */
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        OAuth2User oAuth2User  = (OAuth2User) authentication.getPrincipal();   // 인증 성공 객체에서 OAuth2UserPrincipal을 가져옴
        Map<String, Object> attributes = oAuth2User.getAttributes();
        User user = (User) attributes.get("user"); // attributes 에서 user 엔티티 추출

        //1. 토큰 발급 (Access + Refresh + Redis에 저장)
        TokenResponseDto tokens = tokenService.issueToken(user);
        //2. 리프레시 토큰 쿠키 저장
        addRefreshTokenToCookie(request, response, tokens.refreshToken());

        //3. 인증 관련 설정값, 쿠키 제거
        clearAuthenticationAttributes(request, response);
        //4. 리다이렉트
        String targetUrl = getTargetUrl(tokens.accessToken());
        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }

    private void addRefreshTokenToCookie(HttpServletRequest request, HttpServletResponse response, String refreshToken) {
        int cookieMaxAge = (int) REFRESH_TOKEN_DURATION.toSeconds();
        CookieUtil.deleteCookie(request, response, REFRESH_TOKEN);
        CookieUtil.addCookie(response, REFRESH_TOKEN, refreshToken, cookieMaxAge);
    }

    private void clearAuthenticationAttributes(HttpServletRequest request,
                                               HttpServletResponse response) {
        super.clearAuthenticationAttributes(request);
        oauth2AuthorizationRequestBasedOnCookieRepository.removeAuthorizationRequest(request, response);
    }
    private String getTargetUrl(String token) {
        return UriComponentsBuilder.fromUriString(REDIRECT_URI)
                .queryParam("token", token)
                .build().toUriString();

    }
}
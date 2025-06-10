package com.example.emergencyassistb4b4.auth.controller;

import com.example.emergencyassistb4b4.auth.dto.LoginRequestDto;
import com.example.emergencyassistb4b4.auth.signup.SignUpRequestDto;
import com.example.emergencyassistb4b4.auth.dto.TokenReissueRequestDto;
import com.example.emergencyassistb4b4.auth.signup.SignUpService;
import com.example.emergencyassistb4b4.auth.dto.TokenResponseDto;
import com.example.emergencyassistb4b4.auth.token.TokenService;
import com.example.emergencyassistb4b4.global.response.ApiResponse;
import com.example.emergencyassistb4b4.global.security.CustomUserDetailsService;
import com.example.emergencyassistb4b4.global.status.SuccessStatus;
import com.example.emergencyassistb4b4.user.domain.CustomUserDetails;
import com.example.emergencyassistb4b4.user.domain.User;
import com.example.emergencyassistb4b4.user.dto.UserResponseDto;
import jakarta.servlet.ServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {
    private final SignUpService signUpService;
    private final TokenService tokenService;
    private final AuthenticationManager authenticationManager;

    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<TokenResponseDto>> signup(@Valid
                                                         @RequestBody SignUpRequestDto requestDto, ServletRequest servletRequest) {
        TokenResponseDto token = signUpService.signup(requestDto);
        return ApiResponse.onSuccess(SuccessStatus.SIGNUP_SUCCESS, token);
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<TokenResponseDto>> login(@Valid
                                                               @RequestBody LoginRequestDto requestDto, ServletRequest servletRequest) {
        // 1. 인증 시도
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(requestDto.getEmail(), requestDto.getPassword()));

        // 2. 인증된 사용자 정보
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        UserResponseDto userDto = UserResponseDto.from(userDetails.getUser()); // 이미 DTO 타입이면 바로 사용

        // 3. 토큰 발급은 TokenService가 함
        TokenResponseDto tokens = tokenService.issueToken(userDto);

        // 4. SecurityContext 설정( 선택사항 , JWT 기반이면 보통 생략)
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // 5. 클라이언트에 응답
        return ApiResponse.onSuccess(SuccessStatus.LOGIN_SUCCESS, tokens);


    }

    // 클라이언트의 refresh token을 사용해 access token 과 refresh token 을 재발급 하는 api
    @PostMapping("/reissue")
    public ResponseEntity<ApiResponse<TokenResponseDto>> createNewAccessToken(
            @Valid
            @RequestBody TokenReissueRequestDto request) {
        TokenResponseDto tokenResponseDto = tokenService.reissueAccessToken(request.getRefreshToken());
        return ApiResponse.onSuccess(SuccessStatus.CUSTOM_SUCCESS_STATUS, tokenResponseDto);
    }
}

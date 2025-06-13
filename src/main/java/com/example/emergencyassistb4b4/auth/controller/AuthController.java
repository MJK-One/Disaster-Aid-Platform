package com.example.emergencyassistb4b4.auth.controller;

import com.example.emergencyassistb4b4.auth.dto.LoginRequestDto;
import com.example.emergencyassistb4b4.auth.dto.SignUpRequestDto;
import com.example.emergencyassistb4b4.auth.dto.TokenReissueRequestDto;
import com.example.emergencyassistb4b4.auth.login.LoginService;
import com.example.emergencyassistb4b4.auth.service.LogoutService;
import com.example.emergencyassistb4b4.auth.signup.SignUpService;
import com.example.emergencyassistb4b4.auth.dto.TokenResponseDto;
import com.example.emergencyassistb4b4.auth.token.TokenService;
import com.example.emergencyassistb4b4.global.response.ApiResponse;
import com.example.emergencyassistb4b4.global.security.JwtUtils;
import com.example.emergencyassistb4b4.global.status.SuccessStatus;
import jakarta.servlet.ServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {
    private final SignUpService signUpService;
    private final LoginService loginService;
    private final TokenService tokenService;
    private final LogoutService logoutService;
    private final JwtUtils jwtUtils;

    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<TokenResponseDto>> signup(@Valid
                                                         @RequestBody SignUpRequestDto requestDto, ServletRequest servletRequest) {
        TokenResponseDto token = signUpService.signup(requestDto);
        return ApiResponse.onSuccess(SuccessStatus.SIGNUP_SUCCESS, token);
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<TokenResponseDto>> login(@Valid
                                                               @RequestBody LoginRequestDto requestDto, ServletRequest servletRequest) {

        TokenResponseDto tokens = loginService.login(requestDto);
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

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(ServletRequest servletRequest) {
        String token = jwtUtils.resolveToken(servletRequest);
        logoutService.logout(token);
        return ApiResponse.onSuccess(SuccessStatus.LOGOUT_SUCCESS, null);
    }
}

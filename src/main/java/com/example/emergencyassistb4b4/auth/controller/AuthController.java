package com.example.emergencyassistb4b4.auth.controller;

import com.example.emergencyassistb4b4.auth.signup.SignUpRequestDto;
import com.example.emergencyassistb4b4.auth.dto.TokenReissueRequestDto;
import com.example.emergencyassistb4b4.auth.signup.SignUpService;
import com.example.emergencyassistb4b4.auth.dto.TokenResponseDto;
import com.example.emergencyassistb4b4.auth.token.TokenService;
import com.example.emergencyassistb4b4.global.response.ApiResponse;
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
@RequestMapping("/api/auth")
public class AuthController {
    private final SignUpService signUpService;
    private final TokenService tokenService;

    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<TokenResponseDto>> signup(@Valid
                                                         @RequestBody SignUpRequestDto requestDto, ServletRequest servletRequest) {
        TokenResponseDto token = signUpService.signup(requestDto);
        return ResponseEntity.ok(ApiResponse.onSuccess(SuccessStatus.SIGNUP_SUCCESS, token).getBody());
    }


    // 클라이언트의 refresh token을 사용해 access token 과 refresh token 을 재발급 하는 api
    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<TokenResponseDto>> createNewAccessToken(
            @Valid
            @RequestBody TokenReissueRequestDto request) {
        TokenResponseDto tokenResponseDto = tokenService.reissueAccessToken(request.getRefreshToken());
        return ApiResponse.onSuccess(SuccessStatus.CUSTOM_SUCCESS_STATUS, tokenResponseDto);
    }
}

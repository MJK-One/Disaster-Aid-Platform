package com.example.emergencyassistb4b4.auth.controller;

import com.example.emergencyassistb4b4.auth.dto.CreateAccessTokenRequestDto;
import com.example.emergencyassistb4b4.auth.dto.TokenResponseDto;
import com.example.emergencyassistb4b4.auth.service.TokenService;
import com.example.emergencyassistb4b4.global.response.ApiResponse;
import com.example.emergencyassistb4b4.global.status.SuccessStatus;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 토큰 발급 및 재발급 관련 API를 제공하는 컨트롤러입니다.
 * 클라이언트의 Refresh Token 요청에 따라 Access Token 및 새로운 Refresh Token을 재발급합니다.
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/auth")
public class TokenController {

    private final TokenService tokenService;

    // 클라이언트의 refresh token을 사용해 access token 과 refresh token 을 재발급 하는 api
    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<TokenResponseDto>> createNewAccessToken(
            @Valid
            @RequestBody CreateAccessTokenRequestDto request) {
        TokenResponseDto tokenResponseDto = tokenService.reissueAccessToken(request.getRefreshToken());
       return ApiResponse.onSuccess(SuccessStatus.CUSTOM_SUCCESS_STATUS, tokenResponseDto);
    }

}

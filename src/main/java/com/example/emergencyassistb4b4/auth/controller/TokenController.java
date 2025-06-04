package com.example.emergencyassistb4b4.auth.controller;

import com.example.emergencyassistb4b4.auth.dto.CreateAccessTokenRequest;
import com.example.emergencyassistb4b4.auth.dto.CreateAccessTokenResponse;
import com.example.emergencyassistb4b4.auth.service.TokenService;
import com.example.emergencyassistb4b4.global.response.ApiResponse;
import com.example.emergencyassistb4b4.global.status.BaseCode;
import com.example.emergencyassistb4b4.global.status.BaseErrorCode;
import com.example.emergencyassistb4b4.global.status.SuccessStatus;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class TokenController {
    private final TokenService tokenService;

    @PostMapping("/api/refresh")
    public ResponseEntity<ApiResponse<CreateAccessTokenResponse>> createNewAccessToken(
            @Valid
            @RequestBody CreateAccessTokenRequest request) {
        String newAccessToken = tokenService.createNewAccessToken(request.getRefreshToken());
        CreateAccessTokenResponse responseDto = new CreateAccessTokenResponse(newAccessToken);
        return ApiResponse.onSuccess(SuccessStatus.CUSTOM_SUCCESS_STATUS, responseDto);
    }
}

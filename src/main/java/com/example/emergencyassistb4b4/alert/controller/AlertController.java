package com.example.emergencyassistb4b4.alert.controller;

import com.example.emergencyassistb4b4.alert.dto.response.UserAlert;
import com.example.emergencyassistb4b4.alert.enums.AlertType;
import com.example.emergencyassistb4b4.alert.service.query.AlertQueryService;
import com.example.emergencyassistb4b4.global.response.ApiResponse;
import com.example.emergencyassistb4b4.global.status.SuccessStatus;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/alerts")
@RequiredArgsConstructor
public class AlertController {

    private final AlertQueryService alertQueryService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<UserAlert>>> listAlerts(
        @RequestParam String alertType
//        @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        Long userId = 1L;
        return ApiResponse.onSuccess(SuccessStatus.GET_ALERTS_SUCCESS,
            alertQueryService.listAlerts(AlertType.from(alertType), userId));
    }
}
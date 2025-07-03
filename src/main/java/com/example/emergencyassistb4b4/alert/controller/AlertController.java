package com.example.emergencyassistb4b4.alert.controller;

import com.example.emergencyassistb4b4.alert.dto.response.UserAlert;
import com.example.emergencyassistb4b4.alert.enums.AlertType;
import com.example.emergencyassistb4b4.alert.orchestrator.ReportImmediateAlertOrchestratorService;
import com.example.emergencyassistb4b4.alert.service.query.AlertQueryService;
import com.example.emergencyassistb4b4.global.kafka.dto.DisasterReportedEvent;
import com.example.emergencyassistb4b4.global.response.ApiResponse;
import com.example.emergencyassistb4b4.global.security.CustomUserDetails;
import com.example.emergencyassistb4b4.global.status.SuccessStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/alerts")
@RequiredArgsConstructor
public class AlertController {

    private final AlertQueryService alertQueryService;
    private final ReportImmediateAlertOrchestratorService orchestratorService;

    @GetMapping
    @PreAuthorize("hasRole('IND')")
    public ResponseEntity<ApiResponse<List<UserAlert>>> listAlerts(
        @RequestParam String alertType,
        @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        return ApiResponse.onSuccess(SuccessStatus.GET_ALERTS_SUCCESS,
            alertQueryService.listAlerts(AlertType.from(alertType), userDetails.getUser()));
    }

    @PostMapping
    @PreAuthorize("hasRole('IND')")
    public ResponseEntity<ApiResponse<Void>> test(
        @AuthenticationPrincipal CustomUserDetails userDetails
    ) {

        DisasterReportedEvent build = DisasterReportedEvent.builder()
            .reportId(1002L)
            .reporterId(9L)
            .responderId(2L)
            .disasterType("FLOOD")
            .description("description")
            .province("서울")
            .city("강남")
            .reportedAt(LocalDateTime.now())
            .build();

        orchestratorService.process(build);

        return ApiResponse.onSuccess(SuccessStatus.GET_ALERTS_SUCCESS, null);
    }
}
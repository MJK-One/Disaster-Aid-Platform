package com.example.emergencyassistb4b4.volunteer.controller;

import com.example.emergencyassistb4b4.global.response.ApiResponse;
import com.example.emergencyassistb4b4.global.status.SuccessStatus;
import com.example.emergencyassistb4b4.volunteer.dto.Join.CheckinStatusRequest;
import com.example.emergencyassistb4b4.volunteer.service.VolunteerJoinService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping
public class VolunteerJoinController {

    private final VolunteerJoinService volunteerJoinService;

    @PostMapping("/posts/{postId}/teams/{teamNumber}/apply")
    public ResponseEntity<ApiResponse<Void>> joinTeam(
            @PathVariable Long postId,
            @PathVariable int teamNumber,
            @RequestParam Long userId
    ) {
        volunteerJoinService.joinTeam(postId, teamNumber, userId);
        return ApiResponse.onSuccess(SuccessStatus.VOLUNTEER_CREATE_SUCCESS, null);
    }

    @PatchMapping("/volunteer-participants/{participantId}")
    public  ResponseEntity<ApiResponse<Void>> cancelJoin(
            @PathVariable Long participantId,
            @Valid @RequestBody CheckinStatusRequest request,
            @RequestParam Long userId
    ) {
        volunteerJoinService.cancelJoin(participantId, request, userId);
        return ApiResponse.onSuccess(SuccessStatus.VOLUNTEER_SUCCESS, null);
    }

}
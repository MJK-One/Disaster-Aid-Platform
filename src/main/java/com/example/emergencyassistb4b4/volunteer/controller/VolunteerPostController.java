package com.example.emergencyassistb4b4.volunteer.controller;

import com.example.emergencyassistb4b4.global.response.ApiResponse;
import com.example.emergencyassistb4b4.global.status.SuccessStatus;
import com.example.emergencyassistb4b4.volunteer.dto.Post.CreatePostRequest;
import com.example.emergencyassistb4b4.volunteer.dto.Post.PostTeamsResponse;
import com.example.emergencyassistb4b4.volunteer.dto.Post.UpdatePostRequest;
import com.example.emergencyassistb4b4.volunteer.dto.Post.PostDetailResponse;
import com.example.emergencyassistb4b4.volunteer.service.VolunteerPostService;
import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.shaded.com.google.protobuf.Api;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/post")
public class VolunteerPostController {

    private final VolunteerPostService volunteerPostService;

    @PostMapping
    public ResponseEntity<ApiResponse<Void>> createPost(
            @RequestParam Long userId,
            @Valid @RequestBody CreatePostRequest request) {
        volunteerPostService.createPost(userId, request);
        return ApiResponse.onSuccess(SuccessStatus.VOLUNTEER_CREATE_SUCCESS, null);
    }

    @PatchMapping("/{postId}")
    public ResponseEntity<ApiResponse<Void>> updatePost(
            @RequestParam Long userId,
            @PathVariable Long postId,
            @Valid @RequestBody UpdatePostRequest request) {
        volunteerPostService.updatePost(userId, postId, request);
        return ApiResponse.onSuccess(SuccessStatus.VOLUNTEER_SUCCESS, null);
    }

    @GetMapping("/{postId}")
    public ResponseEntity<ApiResponse<PostDetailResponse>> getPost(@PathVariable Long postId) {
        PostDetailResponse response = volunteerPostService.getPost(postId);
        return ApiResponse.onSuccess(SuccessStatus.VOLUNTEER_SUCCESS, response);
    }

    @GetMapping("/{postId}/teams")
    public ResponseEntity<ApiResponse<PostTeamsResponse>> getTeamStatus(@PathVariable Long postId) {
        PostTeamsResponse response = volunteerPostService.getTeamStatus(postId);
        return ApiResponse.onSuccess(SuccessStatus.VOLUNTEER_SUCCESS, response);
    }

}
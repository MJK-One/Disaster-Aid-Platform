package com.example.emergencyassistb4b4.volunteer.service;

import com.example.emergencyassistb4b4.global.exception.ApiException;
import com.example.emergencyassistb4b4.global.status.ErrorStatus;
import com.example.emergencyassistb4b4.user.domain.User;
import com.example.emergencyassistb4b4.user.repository.UserRepository;
import com.example.emergencyassistb4b4.volunteer.domain.AttendancePolicy;
import com.example.emergencyassistb4b4.volunteer.domain.Post;
import com.example.emergencyassistb4b4.volunteer.domain.VolunteerLocation;
import com.example.emergencyassistb4b4.volunteer.domain.VolunteerTeam;
import com.example.emergencyassistb4b4.volunteer.dto.Post.CreatePostRequest;
import com.example.emergencyassistb4b4.volunteer.dto.Post.PostDetailResponse;
import com.example.emergencyassistb4b4.volunteer.dto.Post.PostTeamsResponse;
import com.example.emergencyassistb4b4.volunteer.dto.Post.UpdatePostRequest;
import com.example.emergencyassistb4b4.volunteer.enums.PostCategory;
import com.example.emergencyassistb4b4.volunteer.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class VolunteerPostService {

    private final UserRepository userRepository;
    private final PostRepository postRepository;

    // 모집 게시글 생성
    @Transactional
    public void createPost(Long userId, CreatePostRequest request) {
        // 유저 검증
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ApiException(ErrorStatus.USER_NOT_FOUND));

        // Post 생성
        Post post = request.toEntity(user);

        // 팀 생성
        List<VolunteerTeam> teams = generateTeams(post, request.getTotalCapacity(), request.getTeamSize());
        post.addTeams(teams);

        // 저장
        postRepository.save(post);
    }

    // 모집 게시글 수정
    @Transactional
    public void updatePost(Long userId, Long postId, UpdatePostRequest request) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ApiException(ErrorStatus.POST_NOT_FOUND));

        // 위치 수정
        post.setLocation(request.getLocation().toEntity());

        // 출석 정책 수정
        post.setAttendancePolicy(request.getAttendancePolicy().toEntity());
    }

    // 모집 게시글 조회
    @Transactional(readOnly = true)
    public PostDetailResponse getPost(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ApiException(ErrorStatus.POST_NOT_FOUND));

        return PostDetailResponse.from(post);
    }

//    @Transactional(readOnly = true)
//    public PostTeamsResponse getTeamStatus(Long postId) {
//    }

    // 팀 생성
    private List<VolunteerTeam> generateTeams(Post post, int totalCapacity, int teamSize) {
        List<VolunteerTeam> volunteerTeams = new ArrayList<>();
        int teamCount = totalCapacity / teamSize;

        for (int i = 0; i < teamCount; i++) {
            VolunteerTeam team = VolunteerTeam.builder()
                    .post(post)
                    .teamNumber(i)
                    .maxCapacity(teamSize)
                    .build();
            volunteerTeams.add(team);
        }
        return volunteerTeams;
    }
}
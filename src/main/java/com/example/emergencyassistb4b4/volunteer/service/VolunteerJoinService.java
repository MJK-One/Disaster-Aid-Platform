package com.example.emergencyassistb4b4.volunteer.service;

import com.example.emergencyassistb4b4.attendance.event.TrackingScheduleEvent;
import com.example.emergencyassistb4b4.attendance.event.TrackingScheduleEventListener;
import com.example.emergencyassistb4b4.attendance.socket.handler.TrackingSocketHandler;
import com.example.emergencyassistb4b4.global.exception.ApiException;
import com.example.emergencyassistb4b4.global.status.ErrorStatus;
import com.example.emergencyassistb4b4.volunteer.domain.VolunteerParticipant;
import com.example.emergencyassistb4b4.volunteer.domain.VolunteerTeam;
import com.example.emergencyassistb4b4.volunteer.dto.Join.CheckinPeriodDto;
import com.example.emergencyassistb4b4.volunteer.dto.Join.CheckinStatusRequest;
import com.example.emergencyassistb4b4.volunteer.dto.Join.VolunteerParticipationResponse;
import com.example.emergencyassistb4b4.volunteer.infra.redis.service.TeamParticipationRedisService;
import com.example.emergencyassistb4b4.volunteer.repository.VolunteerParticipantRepository;
import com.example.emergencyassistb4b4.volunteer.repository.PostRepository;
import com.example.emergencyassistb4b4.volunteer.repository.VolunteerTeamRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class VolunteerJoinService {

    private final VolunteerTeamRepository teamRepository;
    private final VolunteerParticipantRepository participantRepository;
    private final PostRepository postRepository;
    private final TeamParticipationRedisService teamParticipationRedisService;
    private final VolunteerParticipantService participantService;
    private final TrackingSocketHandler trackingSocketHandler;
    private final TrackingScheduleEventListener eventListener;


    // 팀 참가
    @Transactional
    public void joinTeam(Long postId, int teamNumber, Long userId ) {
        LocalDateTime now = LocalDateTime.now();

        // 참가 중인 경우 X
        if (participantRepository.existsActiveParticipation(userId)) {
            throw new ApiException(ErrorStatus.VOLUNTEER_BAD_REQUEST);
        }

        // 출석 시간 지난 경우 X
        CheckinPeriodDto period = postRepository.findCheckinPeriodByPostId(postId)
                .orElseThrow(() -> new ApiException(ErrorStatus.VOLUNTEER_BAD_REQUEST));

        if(now.isAfter(period.checkinStart())) {
            throw new ApiException(ErrorStatus.VOLUNTEER_BAD_REQUEST);
        }

        // 팀 검증
        VolunteerTeam team = teamRepository.findByPost_IdAndTeamNumber(postId, teamNumber)
                .orElseThrow(() -> new ApiException(ErrorStatus.VOLUNTEER_NOT_FOUND));

        // 현재 인원 + + TTL 설정 (checkinEnd 전달)
        teamParticipationRedisService.tryJoinTeam(team.getId(), userId, team.getMaxCapacity(), period.checkinEnd());

        // 팀원 DB 저장
        try {
            participantService.joinSave(userId, team.getId());
        } catch (RuntimeException e) {
            teamParticipationRedisService.cancelJoin(team.getId(), userId);
            throw e;
        }
    }

    // 팀 참가 취소
    @Transactional
    public void cancelJoin(Long participantId, CheckinStatusRequest request, Long userId) {
        LocalDateTime now = LocalDateTime.now();

        // 참가자 존재 + 내 것인지 확인
        VolunteerParticipant participant = participantRepository.findByIdAndUserId(participantId, userId)
                .orElseThrow(() -> new ApiException(ErrorStatus.VOLUNTEER_FORBIDDEN));

        // participant -> postId
        Long postId = participantRepository.findPostIdByParticipantId(participantId)
                .orElseThrow(() -> new ApiException(ErrorStatus.VOLUNTEER_NOT_FOUND));

        // 출석 시간 < 취소 X < 출석 마감 시간
        CheckinPeriodDto period = postRepository.findCheckinPeriodByPostId(postId)
                .orElseThrow(() -> new ApiException(ErrorStatus.VOLUNTEER_NOT_FOUND));

        if(now.isAfter(period.checkinStart()) && now.isBefore(period.checkinEnd())) {
            throw new ApiException(ErrorStatus.VOLUNTEER_BAD_REQUEST);
        }

        // 현재 인원 -
        teamParticipationRedisService.cancelJoin(participant.getId(), userId);

        // 상태 변경
        participant.updateStatus(request.getStatus());

        trackingSocketHandler.removeVolunteerUserMapping(participant.getId());

        // 출석 스케줄 이벤트 트리거
        eventListener.handleTrackingScheduleEvent(new TrackingScheduleEvent(participant.getVolunteerTeam().getId()));
    }

    // 참가 목록
    @Transactional(readOnly = true)
    public List<VolunteerParticipationResponse> getMyParticipation(Long userId) {
        List<VolunteerParticipant> participants = participantRepository.findAllByUserIdWithPostAndTeam(userId);
        return participants.stream()
                .map(VolunteerParticipationResponse::from)
                .toList();
    }
}
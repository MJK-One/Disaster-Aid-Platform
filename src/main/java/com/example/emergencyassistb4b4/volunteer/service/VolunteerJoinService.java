package com.example.emergencyassistb4b4.volunteer.service;

import com.example.emergencyassistb4b4.attendance.event.TrackingScheduleEvent;
import com.example.emergencyassistb4b4.attendance.event.TrackingScheduleEventListener;
import com.example.emergencyassistb4b4.attendance.socket.handler.TrackingSocketHandler;
import com.example.emergencyassistb4b4.global.exception.ApiException;
import com.example.emergencyassistb4b4.global.status.ErrorStatus;
import com.example.emergencyassistb4b4.volunteer.domain.VolunteerParticipant;
import com.example.emergencyassistb4b4.volunteer.domain.VolunteerTeam;
import com.example.emergencyassistb4b4.volunteer.dto.Join.CheckinStatusRequest;
import com.example.emergencyassistb4b4.volunteer.infra.redis.service.TeamParticipationRedisService;
import com.example.emergencyassistb4b4.volunteer.repository.VolunteerParticipantRepository;
import com.example.emergencyassistb4b4.volunteer.repository.VolunteerTeamRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class VolunteerJoinService {

    private final VolunteerTeamRepository teamRepository;
    private final VolunteerParticipantRepository participantRepository;
    private final TeamParticipationRedisService teamParticipationRedisService;
    private final VolunteerParticipantService participantService;
    private final TrackingSocketHandler trackingSocketHandler;
    private final TrackingScheduleEventListener eventListener;

    // 팀 참가
    @Transactional
    public void joinTeam(Long postId, int teamNumber, Long userId ) {
        // 팀 검증
        VolunteerTeam team = teamRepository.findByPost_IdAndTeamNumber(postId, teamNumber)
                .orElseThrow(() -> new ApiException(ErrorStatus.VOLUNTEER_NOT_FOUND));

        // 현재 인원 +
        teamParticipationRedisService.tryJoinTeam(team.getId(), userId, team.getMaxCapacity());

        // 팀원 DB 저장
        participantService.joinSave(userId, team.getId());

    }

    // 팀 참가 취소
    @Transactional
    public void cancelJoin(Long participantId, CheckinStatusRequest request, Long userId) {
        // 팀원 검증
        VolunteerParticipant participant = participantRepository.findById(participantId)
                .orElseThrow(() -> new ApiException(ErrorStatus.VOLUNTEER_NOT_FOUND));

        // 본인 확인
        if (!participant.getUser().getId().equals(userId)) {
            throw new ApiException(ErrorStatus.USER_NOT_FOUND);
        }

        // 현재 인원 -
        teamParticipationRedisService.cancelJoin(participant.getId(), userId);

        // 상태 변경
        participant.updateStatus(request.getStatus());

        trackingSocketHandler.removeVolunteerUserMapping(participant.getId());

        // 출석 스케줄 이벤트 트리거
        eventListener.handleTrackingScheduleEvent(new TrackingScheduleEvent(participant.getVolunteerTeam().getId()));
    }

}
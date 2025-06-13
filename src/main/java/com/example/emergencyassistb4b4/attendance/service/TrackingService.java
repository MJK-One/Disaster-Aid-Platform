package com.example.emergencyassistb4b4.attendance.service;

import com.example.emergencyassistb4b4.attendance.dto.MessageWrapper;
import com.example.emergencyassistb4b4.attendance.dto.SessionState;
import com.example.emergencyassistb4b4.attendance.dto.TrackingSessionDto;
import com.example.emergencyassistb4b4.attendance.publisher.TrackingSessionPublisher;
import com.example.emergencyassistb4b4.global.exception.ApiException;
import com.example.emergencyassistb4b4.volunteer.domain.*;
import com.example.emergencyassistb4b4.volunteer.repository.VolunteerTeamRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static com.example.emergencyassistb4b4.global.status.ErrorStatus.ATTENDANCE_LOCATION_OR_POLICY_MISSING;

@Slf4j
@Service
@RequiredArgsConstructor
public class TrackingService {

    private final TrackingSessionPublisher trackingSessionPublisher;
    private final VolunteerTeamRepository volunteerTeamRepository;

    /**
     * 팀에 대한 위치 추적 세션 예약 시작
     */
    public void scheduleTrackingForTeam(Long teamId) {
        VolunteerTeam team = volunteerTeamRepository.findWithPostAndDetailsById(teamId)
                .orElseThrow();

        Post post = team.getPost();
        VolunteerLocation location = post.getLocation();
        AttendancePolicy policy = post.getAttendancePolicy();

        if (location == null || policy == null) {
            throw new ApiException(ATTENDANCE_LOCATION_OR_POLICY_MISSING);
        }

        List<Long> participantUserIds = team.getParticipants().stream()
                .map(VolunteerParticipant::getId)
                .toList();

        TrackingSessionDto sessionDto = TrackingSessionDto.from(team, location, policy, participantUserIds);

        LocalDateTime startTime = policy.getCheckinStart(); // 위치 추적 시작 시간 기준

        // 1. STARTED 메시지 예약
        scheduleTrackingAtTime(new MessageWrapper(SessionState.READY, sessionDto), startTime);

        // 2. LOCATION_REQUEST 메시지 1분 간격으로 29회 발행
        for (int i = 1; i <= 29; i++) {
            LocalDateTime requestTime = startTime.plusMinutes(i);

            scheduleTrackingAtTime(new MessageWrapper(SessionState.STARTED, sessionDto), requestTime);
        }

        // 3. ENDED 메시지 예약 (30분 뒤)
        LocalDateTime endTime = startTime.plusMinutes(30);

        scheduleTrackingAtTime(new MessageWrapper(SessionState.ENDED, sessionDto), endTime);

        log.debug("Tracking session scheduled: teamId={}, start={}, end={}", teamId, startTime, endTime);
    }

    /**
     * 메시지를 일정 시간 뒤에 발행
     */
    public void scheduleTrackingAtTime(MessageWrapper wrapper, LocalDateTime scheduledTime) {
        long delayMillis = Duration.between(LocalDateTime.now(), scheduledTime).toMillis();

        if (delayMillis < 0) {
            log.warn("Scheduled time {} is in the past. Sending immediately.", scheduledTime);
            delayMillis = 0;
        }

        trackingSessionPublisher.publishDelayedTrackingSession(wrapper, delayMillis);
    }
}

package com.example.emergencyassistb4b4.attendance.service;

import com.example.emergencyassistb4b4.attendance.dto.MessageWrapper;
import com.example.emergencyassistb4b4.attendance.dto.SessionState;
import com.example.emergencyassistb4b4.attendance.dto.TrackingSessionDto;
import com.example.emergencyassistb4b4.attendance.publisher.TrackingSessionPublisher;
import com.example.emergencyassistb4b4.volunteer.domain.*;
import com.example.emergencyassistb4b4.volunteer.repository.VolunteerTeamRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

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
            throw new IllegalStateException("위치 정보나 출석 정책이 설정되지 않았습니다.");
        }

        List<Long> participantUserIds = team.getParticipants().stream()
                .map(VolunteerParticipant::getId)
                .toList();

        LocalDateTime startTime = policy.getCheckinStart(); // 위치 추적 시작 시간 기준

        // 1. STARTED 메시지 예약
        TrackingSessionDto startDto = TrackingSessionDto.from(team, location, policy, participantUserIds);
        scheduleTrackingAtTime(new MessageWrapper(SessionState.READY, startDto), startTime);

        // 2. LOCATION_REQUEST 메시지 1분 간격으로 29회 발행
        for (int i = 1; i <= 29; i++) {
            LocalDateTime requestTime = startTime.plusMinutes(i);
            TrackingSessionDto locationDto = TrackingSessionDto.from(team, location, policy, participantUserIds);

            scheduleTrackingAtTime(new MessageWrapper(SessionState.STARTED, locationDto), requestTime);
        }

        // 3. ENDED 메시지 예약 (30분 뒤)
        LocalDateTime endTime = startTime.plusMinutes(30);
        TrackingSessionDto endDto = TrackingSessionDto.from(team, location, policy, participantUserIds);
        scheduleTrackingAtTime(new MessageWrapper(SessionState.ENDED, endDto), endTime);

        log.info("Tracking session scheduled: teamId={}, start={}, end={}", teamId, startTime, endTime);
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

package com.example.emergencyassistb4b4.attendance.socket.service;

import com.example.emergencyassistb4b4.global.exception.ApiException;
import com.example.emergencyassistb4b4.global.status.ErrorStatus;
import com.example.emergencyassistb4b4.location.service.LocationService;
import com.example.emergencyassistb4b4.volunteer.domain.*;
import com.example.emergencyassistb4b4.volunteer.repository.VolunteerParticipantRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.example.emergencyassistb4b4.global.status.ErrorStatus.VOLUNTEER_NOT_FOUND;


@Slf4j
@Service
@RequiredArgsConstructor
public class LocationWebSocketService {

    private final VolunteerParticipantRepository volunteerParticipantRepository;
    private final LocationService locationService;
    private final RedisTemplate<String, Object> redisTemplate;

    public boolean checkAttendanceForVolunteer(Long volunteerId, double lat, double lon) {
        // 1) volunteerId가 속한 팀, 팀의 위치, 출석 정책 조회
        VolunteerParticipant volunteerParticipant=volunteerParticipantRepository.findById(volunteerId).
                orElseThrow(()-> new ApiException(VOLUNTEER_NOT_FOUND));

        VolunteerTeam team=volunteerParticipant.getVolunteerTeam();

        Post post = team.getPost();
        VolunteerLocation location = post.getLocation();
        AttendancePolicy policy = post.getAttendancePolicy();

        if (location == null || policy == null) {
            throw new ApiException(ErrorStatus.ATTENDANCE_LOCATION_OR_POLICY_MISSING);
        }

        // 2) LocationService의 findUsersWithinRadius를 사용하여 반경 내 존재 여부 확인
        List<Object> usersWithinRadius = locationService.findUsersWithinRadius(
                location.getLocationLat(),
                location.getLocationLng(),
                policy.getAttendanceRadiusMeters()
        );

        // 3) 현재 volunteerId가 반경 내 있는지 확인
        return usersWithinRadius.contains(String.valueOf(volunteerId));
    }

    public void saveAndPublishAttendance(Long volunteerId, boolean isPresent) {
        // Redis에 출석 상태 기록 (예: 리스트에 기록)
        String redisKey = "attendance:session:" +":" + volunteerId;
        redisTemplate.opsForList().rightPush(redisKey, isPresent ? "1" : "0");

    }

}

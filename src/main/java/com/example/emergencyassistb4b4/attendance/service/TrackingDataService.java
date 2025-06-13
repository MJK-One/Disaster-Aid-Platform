package com.example.emergencyassistb4b4.attendance.service;

import com.example.emergencyassistb4b4.global.exception.ApiException;
import com.example.emergencyassistb4b4.volunteer.domain.VolunteerParticipant;
import com.example.emergencyassistb4b4.volunteer.enums.CheckinStatus;
import com.example.emergencyassistb4b4.volunteer.repository.VolunteerParticipantRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.example.emergencyassistb4b4.global.status.ErrorStatus.ATTENDANCE_RECORD_PARSE_FAILED;

@RequiredArgsConstructor
@Slf4j
@Service
public class TrackingDataService {

    private static final String REDIS_ATTENDANCE_KEY_PREFIX = "attendance:session:";

    private final RedisTemplate<String, String> redisTemplate;
    private final VolunteerParticipantRepository participantRepository;

    public void saveSessionAttendanceData(List<Long> volunteerIds, Long teamId) {
        List<VolunteerParticipant> updateList = new ArrayList<>();

        for (Long volunteerId : volunteerIds) {
            String redisKey = REDIS_ATTENDANCE_KEY_PREFIX + volunteerId;
            List<String> records = redisTemplate.opsForList().range(redisKey, 0, -1);
            if (records == null || records.isEmpty()) {
                log.debug("Redis 기록 없음 - volunteerId={}", volunteerId);
                continue;
            }

            long presentCount = records.stream()
                    .map(this::parseRecordToBoolean)
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .filter(present -> present)
                    .count();

            CheckinStatus finalStatus = (presentCount > 20) ? CheckinStatus.PARTICIPATED : CheckinStatus.CHECKED;

            participantRepository.findById(volunteerId).ifPresent(participant -> {
                participant.updateCheckinStatus(finalStatus);
                updateList.add(participant);
            });

            redisTemplate.delete(redisKey);
        }

        participantRepository.saveAll(updateList);
        log.info("참여자 출석 상태 {}건 저장 완료 (teamId={})", updateList.size(), teamId);
    }

    private Optional<Boolean> parseRecordToBoolean(String record) {
        try {
            String[] parts = record.split(":");
            if (parts.length != 2) return Optional.empty();
            return Optional.of("1".equals(parts[1]));
        } catch (Exception e) {
            throw new ApiException(ATTENDANCE_RECORD_PARSE_FAILED);
        }
    }
}

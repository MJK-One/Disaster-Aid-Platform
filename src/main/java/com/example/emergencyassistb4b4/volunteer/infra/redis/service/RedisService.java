package com.example.emergencyassistb4b4.volunteer.infra.redis.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RedisService {

    private final RedisTemplate<String, String> redisTemplate;
    private static final String COUNT_KEY_FORMAT = "team:%d:count";
    private static final String USERS_KEY_FORMAT = "team:%d:users";

    // 현재 인원 +
    public void tryJoinTeam(Long teamId, Long userId, int maxCapacity) {
        String countKey = String.format(COUNT_KEY_FORMAT, teamId);
        String usersKey = String.format(USERS_KEY_FORMAT, teamId);

        DefaultRedisScript<Long> script = new DefaultRedisScript<>();
        script.setScriptText("""
            -- 정원 초과 여부 확인
            if redis.call("SCARD", KEYS[2]) >= tonumber(ARGV[1]) then
                return 0
            end
            -- 중복 참가 여부 확인
            if redis.call("SISMEMBER", KEYS[2], ARGV[2]) == 1 then
                return -1
            end
            -- 유저 ID를 참가자 Set에 추가
            redis.call("SADD", KEYS[2], ARGV[2])
            -- 참가자 수 1 증가
            redis.call("INCR", KEYS[1])
            return 1
        """);
        script.setResultType(Long.class);

        Long result = redisTemplate.execute(
                script,
                List.of(countKey, usersKey), // KEYS[1], KEYS[2]
                String.valueOf(maxCapacity), // ARGV[1] = 최대 수용 인원
                String.valueOf(userId)       // ARGV[2] = 참가 요청 유저 ID

        );

        if (result == null) {
            throw new RuntimeException("Redis Lua 스크립트 실행 실패");
        }

        switch (result.intValue()) {
            case 1 -> {} // 성공
            case 0 -> throw new RuntimeException("팀 정원이 초과되었습니다: teamId=" + teamId);
            case -1 -> throw new RuntimeException("이미 참가한 유저입니다: userId=" + userId + ", teamId=" + teamId);
            default -> throw new IllegalStateException("예상치 못한 Redis 반환값: " + result);
        }
    }

    // 현재 인원 -
    public void cancelJoin(Long teamId, Long userId) {
        String countKey = String.format(COUNT_KEY_FORMAT, teamId);
        String usersKey = String.format(USERS_KEY_FORMAT, teamId);

        redisTemplate.opsForSet().remove(usersKey, String.valueOf(userId));
        redisTemplate.opsForValue().decrement(countKey);
    }

    // 현재 인원 조회
    public int getCurrentCount(Long teamId) {
        String countKey = String.format(COUNT_KEY_FORMAT, teamId);
        String count = redisTemplate.opsForValue().get(countKey);
        return count != null ? Integer.parseInt(count) : 0;
    }
}
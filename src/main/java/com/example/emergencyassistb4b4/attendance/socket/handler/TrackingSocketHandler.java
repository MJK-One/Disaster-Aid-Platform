package com.example.emergencyassistb4b4.attendance.socket.handler;

import com.example.emergencyassistb4b4.global.exception.ApiException;
import com.example.emergencyassistb4b4.volunteer.domain.VolunteerParticipant;
import com.example.emergencyassistb4b4.volunteer.domain.VolunteerTeam;
import com.example.emergencyassistb4b4.volunteer.repository.VolunteerTeamRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;

import java.net.URI;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import static com.example.emergencyassistb4b4.global.status.ErrorStatus.WEBSOCKET_MESSAGE_SEND_FAILED;
import static com.example.emergencyassistb4b4.global.status.ErrorStatus.WEBSOCKET_MESSAGE_SERIALIZATION_FAILED;

@Slf4j
@Component
@RequiredArgsConstructor
public class TrackingSocketHandler implements WebSocketHandler {

    private final Map<Long, Set<WebSocketSession>> userSessions = new ConcurrentHashMap<>(); // volunteerId → sessions
    private final VolunteerTeamRepository volunteerTeamRepository;
    // teamId → volunteerIds
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        log.info("WebSocket 연결됨: {}", session.getId());

        Long volunteerId = extractVolunteerIdFromQueryParam(session);
        if (volunteerId != null) {
            registerSession(volunteerId, session);
            log.info("volunteerId {}에 대해 세션 {} 등록 완료", volunteerId, session.getId());
        } else {
            log.warn("인증 실패 또는 volunteerId 누락. 세션 종료: {}", session.getId());
            try {
                session.close(CloseStatus.NOT_ACCEPTABLE.withReason("Unauthorized"));
            } catch (Exception e) {
                log.error("세션 강제 종료 실패: {}", e.getMessage());
            }
        }
    }

    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) {
        // 메시지 수신 처리 필요 없으면 생략
        log.debug("수신된 메시지: {}", message.getPayload());
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) {
        log.error("WebSocket 오류: {}", exception.getMessage());
        try {
            if (session.isOpen()) {
                session.close(CloseStatus.SERVER_ERROR);
            }
        } catch (Exception e) {
            log.error("세션 닫기 실패: {}", e.getMessage());
        }
        removeSession(session);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        log.info("WebSocket 연결 종료: {}", session.getId());
        removeSession(session);
    }

    @Override
    public boolean supportsPartialMessages() {
        return false;
    }

    public void registerSession(Long volunteerId, WebSocketSession session) {
        userSessions.computeIfAbsent(volunteerId, k -> ConcurrentHashMap.newKeySet()).add(session);
    }

    public void removeSession(WebSocketSession session) {
        userSessions.values().forEach(sessions -> sessions.remove(session));
        userSessions.entrySet().removeIf(entry -> entry.getValue().isEmpty());
    }

    public void sendTrackingStatus(Long volunteerId, Object statusMessage) {
        Set<WebSocketSession> sessions = userSessions.get(volunteerId);
        if (sessions != null) {
            sessions.removeIf(session -> !session.isOpen());
            sessions.forEach(session -> {
                try {
                    String json = objectMapper.writeValueAsString(statusMessage);
                    session.sendMessage(new TextMessage(json));
                } catch (Exception e) {
                    log.error("WebSocket 메시지 전송 실패: {}", e.getMessage());
                }
            });
            if (sessions.isEmpty()) {
                userSessions.remove(volunteerId);
            }
        } else {
            log.warn("volunteerId {}에 대한 활성 세션 없음", volunteerId);
        }
    }

    public void sendToTeam(Long teamId, String event, Object payload) {

        Optional<VolunteerTeam> team = volunteerTeamRepository.findWithPostAndDetailsById(teamId);
        if (team.isEmpty()) {
            log.warn("teamId={}에 대한 volunteerId 없음", teamId);
            return;
        }

        Set<Long> volunteerIds = team.get()
                .getParticipants()
                .stream()
                .map(VolunteerParticipant::getId)
                .collect(Collectors.toSet());

        String json;
        try {
            json = objectMapper.writeValueAsString(Map.of("type", event, "data", payload));
        } catch (Exception e) {
            log.error("WebSocket 메시지 직렬화 실패", e);
            return;
        }


        for (Long volunteerId : volunteerIds) {
            Set<WebSocketSession> sessions = userSessions.get(volunteerId);
            if (sessions == null) continue;

            sessions.removeIf(session -> !session.isOpen());
            for (WebSocketSession session : sessions) {
                try {
                    session.sendMessage(new TextMessage(json));
                } catch (Exception e) {
                    throw new ApiException(WEBSOCKET_MESSAGE_SERIALIZATION_FAILED);
                }
            }
        }
    }

    private Long extractVolunteerIdFromQueryParam(WebSocketSession session) {
        try {
            URI uri = session.getUri();
            if (uri == null) return null;

            String query = uri.getQuery();
            if (query == null) return null;

            for (String param : query.split("&")) {
                String[] parts = param.split("=");
                if (parts.length == 2 && parts[0].equals("volunteerId")) {
                    return Long.parseLong(parts[1]);
                }
            }
        } catch (Exception e) {
            throw new ApiException(WEBSOCKET_MESSAGE_SEND_FAILED);
        }
        return null;
    }
}

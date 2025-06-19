package com.example.emergencyassistb4b4.global.status;

import com.example.emergencyassistb4b4.global.response.ErrorReasonDto;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorStatus implements BaseErrorCode {
    //공통
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "C001", "서버 오류입니다."),
    INVALID_REQUEST(HttpStatus.BAD_REQUEST, "C002", "잘못된 요청입니다."),
    FORBIDDEN(HttpStatus.FORBIDDEN, "C003", "권한이 없습니다."),

    // 인증 및 인가
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "AU001","인증이 필요합니다."),
    INVALID_ACCESS_TOKEN(HttpStatus.UNAUTHORIZED, "AU002","유효하지 않은 액세스 토큰입니다."),
    EXPIRED_ACCESS_TOKEN(HttpStatus.UNAUTHORIZED, "AU003","액세스 토큰이 만료되었습니다."),
    INVAlID_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "AU004","유효하지 않은 리프레시 토큰입니다."),
    REFRESH_TOKEN_NOT_FOUND(HttpStatus.UNAUTHORIZED, "AU005","리프레시 토큰을 찾을 수 없습니다."),
    TOKEN_USER_MISMATCH(HttpStatus.UNAUTHORIZED, "AU006","토큰의 사용자 정보가 일치하지 않습니다."),


    //회원가입 및 로그인
    DUPLICATED_EMAIL(HttpStatus.CONFLICT, "AU007", "이미 존재하는 이메일입니다."),
    INVALID_PASSWORD(HttpStatus.BAD_REQUEST, "AU008", "비밀번호가 일치하지 않습니다."),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "AU009", "해당 사용자를 찾을 수 없습니다."),
    OAUTH_PROVIDER_MISMATCH(HttpStatus.BAD_REQUEST, "AU010", "다른 OAuth 제공자로 가입된 계정입니다."),
    OAUTH_LOGIN_ONLY(HttpStatus.BAD_REQUEST, "AU015", "소셜 로그인으로 가입된 계정입니다. 자체 로그인 불가합니다."),
    SELF_LOGIN_ONLY(HttpStatus.BAD_REQUEST, "AU016", "자체 로그인으로 가입된 계정입니다. 소셜 로그인 불가합니다."),
    SIGNUP_STRATEGY_NOT_FOUND(HttpStatus.BAD_REQUEST, "AU017", "지원하지 않는 회원가입 방식입니다."),
    LOGIN_STRATEGY_NOT_FOUND(HttpStatus.BAD_REQUEST, "AU018", " 지원하지 않는 로그인 방식입니다."),
    BUSINESS_NUMBER_REQUIRED(HttpStatus.BAD_REQUEST, "AU019" ,"이 필드는 필수값입니다."),
    INVALID_SIGNUP_REQUEST(HttpStatus.BAD_REQUEST, "AU020", "유효하지 않은 회원가입 요청입니다."),
    COOKIE_NOT_FOUND(HttpStatus.BAD_REQUEST, "AU021", "쿠키가 존재하지 않습니다."),
    INVALID_OBJECT_TYPE(HttpStatus.BAD_REQUEST, "AU022", "역직렬화된 객체 타입이 일치하지 않음."),
    DESERIALIZATION_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "AU023", "역직렬화 중 오류 발생"),


    // 로그아웃
    LOGOUT_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "AU001", "로그아웃 처리에 실패했습니다."),
    CUSTOM_ERROR_STATUS(HttpStatus.INTERNAL_SERVER_ERROR, "C001", "Custom Error"),
    // Redis
    REDIS_SAVE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "R001", "Redis 에 토큰 저장 실패"),
    REDIS_DELETE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "R002", "Redis 에서 토큰 삭제 실패"),
    INVALID_TTL(HttpStatus.BAD_REQUEST, "AU024", "TTL 값은 0보다 커야 합니다."),

    // 자원봉사
    VOLUNTEER_INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "VO010", "VOLUNTEER_INTERNAL_SERVER_ERROR"),
    VOLUNTEER_CONFLICT(HttpStatus.CONFLICT, "VO008", "VOLUNTEER_CONFLICT"),
    VOLUNTEER_BAD_REQUEST(HttpStatus.BAD_REQUEST, "VO000", "VOLUNTEER_BAD_REQUEST"),
    VOLUNTEER_NOT_FOUND(HttpStatus.NOT_FOUND, "VO004", "VOLUNTEER_NOT_FOUND"),

    // 신고
    REPORT_NOT_FOUND(HttpStatus.NOT_FOUND, "RP007", "Do not found."),
    REPORT_UNAUTHORIZED_ACCESS(HttpStatus.UNAUTHORIZED, "RP005", "No permissions on this report."),
    ALERT_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "AL010", "Failed to send message."),
    NOT_FOUND_LOCATION(HttpStatus.NOT_FOUND,"LC004","위치를 찾지 못했습니다"),
    USER_DEVICE_NOT_FOUND(HttpStatus.NOT_FOUND, "RP004", "유저 디바이스가 존재하지 않습니다."),


    KAKAO_API_REQUEST_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "LC010", "카카오 API 요청 실패"),
    KAKAO_API_RESPONSE_PARSE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "LC010", "카카오 API 응답 파싱 실패"),
    KAKAO_API_RESPONSE_STATUS_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "LC010", "카카오 API 비정상 응답"),


    // 자원봉사
    POST_NOT_FOUND(HttpStatus.NOT_FOUND, "VO004", "존재하지 않는 게시글입니다."),

    ATTENDANCE_RECORD_PARSE_FAILED(HttpStatus.BAD_REQUEST, "VO004", "출석 기록 파싱 실패"),
    WEBSOCKET_MESSAGE_SERIALIZATION_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "VO010", "WebSocket 메시지 직렬화 실패"),
    WEBSOCKET_MESSAGE_SEND_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "VO010", "WebSocket 메시지 전송 실패"),
    ATTENDANCE_LOCATION_OR_POLICY_MISSING(HttpStatus.BAD_REQUEST, "VO004","위치 정보나 출석 정책이 설정되지 않았습니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
    private final ErrorReasonDto cachedErrorReasonDto;

    ErrorStatus(HttpStatus httpStatus, String code, String message) {
        this.httpStatus = httpStatus;
        this.code = code;
        this.message = message;
        this.cachedErrorReasonDto = ErrorReasonDto.builder()
            .isSuccess(false)
            .httpStatus(httpStatus)
            .code(code)
            .message(message)
            .build();
    }

    @Override
    public ErrorReasonDto getReasonHttpStatus() {
        return cachedErrorReasonDto;
    }
}
package com.example.emergencyassistb4b4.global.status;

import com.example.emergencyassistb4b4.global.response.ErrorReasonDto;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorStatus implements BaseErrorCode {

    CUSTOM_ERROR_STATUS(HttpStatus.INTERNAL_SERVER_ERROR, "C001", "Custom Error"),
    // 유저
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "US004", "존재하지 않는 사용자입니다."),
    USER_UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "US005", "권한 없음."),

    // 자원봉사
    VOLUNTEER_NOT_FOUND(HttpStatus.NOT_FOUND, "VO004", "VOLUNTEER_NOT_FOUND");

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
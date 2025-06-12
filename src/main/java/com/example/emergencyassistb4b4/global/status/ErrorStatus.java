package com.example.emergencyassistb4b4.global.status;

import com.example.emergencyassistb4b4.global.response.ErrorReasonDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorStatus implements BaseErrorCode {

    REPORT_NOT_FOUND(HttpStatus.NOT_FOUND, "RP007", "Do not found."),
    REPORT_UNAUTHORIZED_ACCESS(HttpStatus.UNAUTHORIZED, "RP005", "No permissions on this report."),

    ALERT_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "AL010", "Failed to send message.");

    KAKAO_API_REQUEST_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "LC010", "카카오 API 요청 실패"),
    KAKAO_API_RESPONSE_PARSE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "LC010", "카카오 API 응답 파싱 실패"),
    KAKAO_API_RESPONSE_STATUS_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "LC010", "카카오 API 비정상 응답");



    REPORT_NOT_FOUND(HttpStatus.NOT_FOUND, "RP001", "Do not found."),
    PEPORT_UNAUTHORIZED_ACCESS(HttpStatus.UNAUTHORIZED, "RP002", "No permissions on this report.");


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
package com.example.emergencyassistb4b4.global.status;

import com.example.emergencyassistb4b4.global.response.ReasonDto;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum SuccessStatus implements BaseCode {

    //인증
    LOGIN_SUCCESS(HttpStatus.OK, "S1000", "로그인에 성공했습니다."),
    LOGOUT_SUCCESS(HttpStatus.OK,"S1002", "로그아웃에 성공했습니다."),
    SIGNUP_SUCCESS(HttpStatus.OK, "S1009" ,"로그인에 성공했습니다"),
    TOKEN_REISSUE_SUCCESS(HttpStatus.CREATED, "S1003", "액세스 토큰 재발급에 성공했습니다."),
    CUSTOM_SUCCESS_STATUS(HttpStatus.OK, "S1001", "Custom Success");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
    private final ReasonDto cachedReasonDto;

    SuccessStatus(HttpStatus httpStatus, String code, String message) {
        this.httpStatus = httpStatus;
        this.code = code;
        this.message = message;
        this.cachedReasonDto = ReasonDto.builder()
            .isSuccess(true)
            .httpStatus(httpStatus)
            .code(code)
            .message(message)
            .build();
    }

    @Override
    public ReasonDto getReasonHttpStatus() {
        return cachedReasonDto;
    }
}

package com.example.emergencyassistb4b4.global.status;

import com.example.emergencyassistb4b4.global.response.ReasonDto;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum SuccessStatus implements BaseCode {

    CUSTOM_SUCCESS_STATUS(HttpStatus.OK, "S1001", "Custom Success"),

    //Volunteer
    POST_CREATE_SUCCESS(HttpStatus.CREATED, "VO001", "자원봉사 모집글이 성공적으로 생성되었습니다."),
    POST_UPDATE_SUCCESS(HttpStatus.OK, "VO002", "자원봉사 모집글이 성공적으로 수정되었습니다."),
    POST_READ_SUCCESS(HttpStatus.OK, "VO002", "자원봉사 모집글 상세 내역이 정상적으로 조회되었습니다.");

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

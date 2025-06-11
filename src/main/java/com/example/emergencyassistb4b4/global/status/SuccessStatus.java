package com.example.emergencyassistb4b4.global.status;

import com.example.emergencyassistb4b4.global.response.ReasonDto;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum SuccessStatus implements BaseCode {

    CUSTOM_SUCCESS_STATUS(HttpStatus.OK, "S1001", "Custom Success"),

    LOCATION_SAVE_SUCCESS(HttpStatus.CREATED, "LC002", "Location information save is success"),
    SHELTER_SEARCH_SUCCESS(HttpStatus.OK, "LC001", "Shelter search completed successfully"),
    DISASTER_SEARCH_SUCCESS(HttpStatus.OK, "LC001", "Disaster summary search completed successfully");

    REPORT_CREATE_SUCCESS(HttpStatus.CREATED, "RP001", "Report completed"),
    REPORT_GET_SUCCESS(HttpStatus.OK, "RP002", "Report inquiry completed"),
    REPORT_REPORTER_GET_SUCCESS(HttpStatus.OK, "RP003", "Reporter inquiry completed");


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

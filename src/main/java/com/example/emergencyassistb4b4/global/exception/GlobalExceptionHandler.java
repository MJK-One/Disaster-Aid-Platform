package com.example.emergencyassistb4b4.global.exception;

import com.example.emergencyassistb4b4.global.exception.dto.FieldErrorDetail;
import com.example.emergencyassistb4b4.global.response.ApiResponse;
import com.example.emergencyassistb4b4.global.response.ErrorReasonDto;
import com.example.emergencyassistb4b4.global.status.ErrorStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.List;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ApiResponse<ErrorReasonDto>> handleApiException(ApiException e) {
        return ApiResponse.onFailure(e.getErrorCode());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<List<FieldErrorDetail>>> handleValidationException(MethodArgumentNotValidException ex) {

        List<FieldErrorDetail> fieldErrors = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> FieldErrorDetail.of(
                        error.getField(),
                        error.getRejectedValue(),
                        error.getDefaultMessage()
                ))
                .toList();

        // 실패 응답: ErrorStatus.INVALID_REQUEST 사용
        return ResponseEntity.status(ErrorStatus.INVALID_REQUEST.getHttpStatus())
                .body(new ApiResponse<>(
                        false,
                        ErrorStatus.INVALID_REQUEST.getCode(),
                        "유효하지 않은 요청입니다.",
                        fieldErrors
                ));
    }
}
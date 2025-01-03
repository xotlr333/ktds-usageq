package com.telco.common.exception;

import com.telco.common.dto.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(InvalidUserException.class)
    public ResponseEntity<ApiResponse<Void>> handleInvalidUserException(InvalidUserException e) {
        return ResponseEntity
                .status(404)
                .body(ApiResponse.error(404, e.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleGeneralException(Exception e) {
        log.error("Unexpected error occurred", e);
        return ResponseEntity
                .status(500)
                .body(ApiResponse.error(500, "시스템 오류가 발생했습니다."));
    }
}
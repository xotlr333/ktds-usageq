package com.telco.management.api.exception;

import com.telco.common.dto.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

@Slf4j
@RestControllerAdvice("com.telco.management.api")  // 스캔 범위를 api 패키지로 한정
public class ApiExceptionHandler {

    @ExceptionHandler(BizException.class)
    public ResponseEntity<ApiResponse<Void>> handleBizException(
            BizException e, HttpServletRequest request) {
        log.error("Business Exception occurred: {}", e.getMessage());

        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(e.getErrorCode())
                .message(e.getMessage())
                .path(request.getRequestURI())
                .build();

        return ResponseEntity
                .status(e.getErrorCode())
                .body(ApiResponse.error(e.getErrorCode(), e.getMessage()));
    }

    @ExceptionHandler(AmqpException.class)
    public ResponseEntity<ApiResponse<Void>> handleAmqpException(
            AmqpException e, HttpServletRequest request) {
        log.error("RabbitMQ Exception occurred: {}", e.getMessage());

        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(500)
                .message("메시지 큐 처리 중 오류가 발생했습니다")
                .path(request.getRequestURI())
                .build();

        return ResponseEntity
                .status(500)
                .body(ApiResponse.error(500, "메시지 큐 처리 중 오류가 발생했습니다"));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleGeneralException(
            Exception e, HttpServletRequest request) {
        log.error("Unexpected Exception occurred: {}", e.getMessage());

        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(500)
                .message("내부 서버 오류가 발생했습니다")
                .path(request.getRequestURI())
                .build();

        return ResponseEntity
                .status(500)
                .body(ApiResponse.error(500, "내부 서버 오류가 발생했습니다"));
    }
}

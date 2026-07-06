package com.clinic.booking.exception;

import com.clinic.booking.dto.common.ErrorResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // 1. Xử lý các lỗi nghiệp vụ (AppException)
    @ExceptionHandler(value = AppException.class)
    public ResponseEntity<ErrorResponse> handlingAppException(AppException exception) {
        ErrorCode errorCode = exception.getErrorCode();
        ErrorResponse errorResponse = ErrorResponse.builder()
                .code(errorCode.getCode())
                .message(errorCode.getMessage())
                .timestamp(LocalDateTime.now())
                .build();
        return ResponseEntity.status(errorCode.getStatusCode()).body(errorResponse);
    }

    // 2. Xử lý lỗi Validate dữ liệu (Ví dụ: @NotBlank, @Size...)
    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handlingValidationException(MethodArgumentNotValidException exception) {
        String errorMessage = exception.getFieldError() != null ? exception.getFieldError().getDefaultMessage() : "Dữ liệu không hợp lệ";
        
        ErrorResponse errorResponse = ErrorResponse.builder()
                .code(ErrorCode.INVALID_KEY.getCode())
                .message(errorMessage)
                .timestamp(LocalDateTime.now())
                .build();
        return ResponseEntity.status(ErrorCode.INVALID_KEY.getStatusCode()).body(errorResponse);
    }

    // 3. Xử lý tất cả các lỗi Hệ thống khác chưa lường trước được (NullPointerException, DataAccessException...)
    @ExceptionHandler(value = Exception.class)
    public ResponseEntity<ErrorResponse> handlingRuntimeException(Exception exception) {
        log.error("Unhandled Exception: ", exception);
        
        ErrorResponse errorResponse = ErrorResponse.builder()
                .code(ErrorCode.UNCATEGORIZED_EXCEPTION.getCode())
                .message(ErrorCode.UNCATEGORIZED_EXCEPTION.getMessage())
                .timestamp(LocalDateTime.now())
                .build();
        return ResponseEntity.status(ErrorCode.UNCATEGORIZED_EXCEPTION.getStatusCode()).body(errorResponse);
    }

    @ExceptionHandler(value = AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handlingAccessDeniedException(AccessDeniedException exception){
        log.error("Access Denied", exception);

        ErrorResponse errorResponse = ErrorResponse.builder()
                .code(ErrorCode.UNAUTHORIZED.getCode())
                .message(ErrorCode.UNAUTHORIZED.getMessage())
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity.status(ErrorCode.UNAUTHORIZED.getStatusCode()).body(errorResponse);
    }

}

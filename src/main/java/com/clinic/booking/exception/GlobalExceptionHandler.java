package com.clinic.booking.exception;

import com.clinic.booking.dto.common.ApiResponse;
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
    public ResponseEntity<ApiResponse<Void>> handlingAppException(AppException exception) {
        ErrorCode errorCode = exception.getErrorCode();
        ApiResponse<Void> apiResponse = ApiResponse.<Void>builder()
                .code(errorCode.getCode())
                .message(errorCode.getMessage())
                .build();
        return ResponseEntity.status(errorCode.getStatusCode()).body(apiResponse);
    }

    // 2. Xử lý lỗi Validate dữ liệu (Ví dụ: @NotBlank, @Size...)
    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handlingValidationException(MethodArgumentNotValidException exception) {
        String errorMessage = exception.getFieldError() != null ? exception.getFieldError().getDefaultMessage() : "Dữ liệu không hợp lệ";
        
        ApiResponse<Void> apiResponse = ApiResponse.<Void>builder()
                .code(ErrorCode.INVALID_KEY.getCode())
                .message(errorMessage)
                .build();
        return ResponseEntity.status(ErrorCode.INVALID_KEY.getStatusCode()).body(apiResponse);
    }

    // 3. Xử lý tất cả các lỗi Hệ thống khác chưa lường trước được (NullPointerException, DataAccessException...)
    @ExceptionHandler(value = Exception.class)
    public ResponseEntity<ApiResponse<Void>> handlingRuntimeException(Exception exception) {
        log.error("Unhandled Exception: ", exception);
        
        ApiResponse<Void> apiResponse = ApiResponse.<Void>builder()
                .code(ErrorCode.UNCATEGORIZED_EXCEPTION.getCode())
                .message(ErrorCode.UNCATEGORIZED_EXCEPTION.getMessage())
                .build();
        return ResponseEntity.status(ErrorCode.UNCATEGORIZED_EXCEPTION.getStatusCode()).body(apiResponse);
    }

    @ExceptionHandler(value = AccessDeniedException.class)
    public ResponseEntity<ApiResponse<Void>> handlingAccessDeniedException(AccessDeniedException exception){
        log.error("Access Denied", exception);

        ApiResponse<Void> apiResponse = ApiResponse.<Void>builder()
                .code(ErrorCode.UNAUTHORIZED.getCode())
                .message(ErrorCode.UNAUTHORIZED.getMessage())
                .build();

        return ResponseEntity.status(ErrorCode.UNAUTHORIZED.getStatusCode()).body(apiResponse);
    }

}

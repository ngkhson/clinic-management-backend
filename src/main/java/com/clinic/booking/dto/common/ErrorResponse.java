package com.clinic.booking.dto.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL) // Chỉ trả về các field có giá trị, bỏ qua null
public class ErrorResponse {
    private int code;
    private String message;
    private LocalDateTime timestamp;
}

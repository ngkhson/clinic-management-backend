package com.clinic.booking.dto.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse <T> {
    @Builder.Default
    int code = 1000;
    String message;
    T result;

    public static <T> ApiResponse<T> success(T result) {
        return ApiResponse.<T>builder().result(result).build();
    }

    public static <T> ApiResponse<T> success() {
        return ApiResponse.<T>builder().build();
    }
}

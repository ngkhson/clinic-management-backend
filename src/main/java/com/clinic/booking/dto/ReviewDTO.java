package com.clinic.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor // QUAN TRỌNG: Bắt buộc phải có để Spring Boot parse được JSON
@AllArgsConstructor // Cần đi kèm khi đã dùng @NoArgsConstructor cùng với @Builder
public class ReviewDTO {
    private Long id;
    private Long appointmentId;
    private Long doctorId;
    private String patientName;
    private Integer rating;
    private String comment;
    private String createdAt;
}
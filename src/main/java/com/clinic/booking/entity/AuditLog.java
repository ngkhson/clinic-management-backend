package com.clinic.booking.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "audit_logs")
public class AuditLog {
    
    @Id
    private String id;
    
    private String userEmail;
    
    private String action; // CREATE, UPDATE, DELETE
    
    private String entityName; // Medicine, Specialty, Appointment
    
    private String details; // Nội dung chi tiết (Ví dụ: "Cập nhật giá thuốc Paracetamol thành 5000")
    
    private LocalDateTime timestamp;
}

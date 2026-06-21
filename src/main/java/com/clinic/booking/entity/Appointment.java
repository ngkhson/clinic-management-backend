package com.clinic.booking.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "appointments")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Appointment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Bệnh nhân đặt lịch (Liên kết tới bảng users)
    @ManyToOne
    @JoinColumn(name = "patient_id", nullable = false)
    private User patient;

    // Bác sĩ được đặt lịch
    @ManyToOne
    @JoinColumn(name = "doctor_id", nullable = false)
    private Doctor doctor;

    // Khung giờ khám (Giả định bạn đã tạo class Schedule)
    @ManyToOne
    @JoinColumn(name = "schedule_id", nullable = false)
    private Schedule schedule;

    @Column(name = "appointment_date", nullable = false)
    private LocalDate appointmentDate;

    @Column(nullable = false)
    private String status; // PENDING, CONFIRMED, COMPLETED, CANCELED

    @Column(columnDefinition = "TEXT")
    private String symptoms;

    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;
}
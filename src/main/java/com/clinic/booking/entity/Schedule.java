package com.clinic.booking.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "schedules")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Schedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Quan hệ N-1: Nhiều khung giờ thuộc về một bác sĩ
    @ManyToOne
    @JoinColumn(name = "doctor_id", nullable = false)
    private Doctor doctor;

    @Column(name = "work_date", nullable = false)
    private LocalDate workDate;

    @Column(name = "time_slot", nullable = false)
    private String timeSlot;

    @Column(name = "max_patients", nullable = false)
    private Integer maxPatients;

    @Column(name = "current_patients", nullable = false)
    @Builder.Default
    private Integer currentPatients = 0; // Đặt giá trị mặc định là 0
}
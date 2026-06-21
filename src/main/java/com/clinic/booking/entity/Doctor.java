package com.clinic.booking.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(name = "doctors")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Doctor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Quan hệ 1-1: Một bác sĩ tương ứng với một tài khoản user
    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    private User user;

    // Quan hệ N-1: Nhiều bác sĩ có thể thuộc cùng một chuyên khoa
    @ManyToOne
    @JoinColumn(name = "specialty_id", referencedColumnName = "id")
    private Specialty specialty;

    private String degree;

    @Column(columnDefinition = "TEXT")
    private String biography;

    @Column(name = "examination_price", nullable = false)
    private BigDecimal examinationPrice;
}
package com.clinic.booking.service;

import com.clinic.booking.dto.ReviewDTO;
import com.clinic.booking.entity.Appointment;
import com.clinic.booking.entity.Review;
import com.clinic.booking.repository.AppointmentRepository;
import com.clinic.booking.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final AppointmentRepository appointmentRepository;

    public ReviewDTO createReview(ReviewDTO request) {
        // Kiểm tra xem đã đánh giá chưa
        if (reviewRepository.findByAppointmentId(request.getAppointmentId()).isPresent()) {
            throw new RuntimeException("Bạn đã gửi đánh giá cho ca khám này rồi!");
        }

        Appointment appointment = appointmentRepository.findById(request.getAppointmentId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy lịch hẹn!"));

        Review review = Review.builder()
                .appointment(appointment)
                .doctor(appointment.getDoctor())
                .patient(appointment.getPatient())
                .rating(request.getRating())
                .comment(request.getComment())
                .build();

        review = reviewRepository.save(review);
        return mapToDTO(review);
    }

    public List<ReviewDTO> getDoctorReviews(Long doctorId) {
        return reviewRepository.findByDoctorIdOrderByCreatedAtDesc(doctorId)
                .stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    private ReviewDTO mapToDTO(Review review) {
        return ReviewDTO.builder()
                .id(review.getId())
                .appointmentId(review.getAppointment().getId())
                .doctorId(review.getDoctor().getId())
                .patientName(review.getPatient().getFullName())
                .rating(review.getRating())
                .comment(review.getComment())
                .createdAt(review.getCreatedAt().toString())
                .build();
    }
}
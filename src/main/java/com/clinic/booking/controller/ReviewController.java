package com.clinic.booking.controller;

import com.clinic.booking.dto.ReviewDTO;
import com.clinic.booking.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reviews")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    // API: Gửi đánh giá mới
    @PostMapping
    public ResponseEntity<?> createReview(@RequestBody ReviewDTO request) {
        try {
            return ResponseEntity.ok(reviewService.createReview(request));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // API: Lấy danh sách đánh giá của Bác sĩ (Công khai)
    @GetMapping("/doctor/{doctorId}")
    public ResponseEntity<List<ReviewDTO>> getDoctorReviews(@PathVariable Long doctorId) {
        return ResponseEntity.ok(reviewService.getDoctorReviews(doctorId));
    }
}
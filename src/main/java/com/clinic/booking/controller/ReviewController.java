package com.clinic.booking.controller;

import com.clinic.booking.dto.review.ReviewRequest;
import com.clinic.booking.dto.common.ApiResponse;
import com.clinic.booking.dto.review.ReviewResponse;
import com.clinic.booking.service.ReviewService;
import lombok.RequiredArgsConstructor;
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
    public ApiResponse<Object> createReview(@RequestBody ReviewRequest request) {
        return ApiResponse.success(reviewService.createReview(request));
    }

    // API: Lấy danh sách đánh giá của Bác sĩ (Công khai)
    @GetMapping("/doctor/{doctorId}")
    public ApiResponse<List<ReviewResponse>> getDoctorReviews(@PathVariable Long doctorId) {
        return ApiResponse.success(reviewService.getDoctorReviews(doctorId));
    }
}
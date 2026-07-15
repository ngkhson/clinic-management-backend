package com.clinic.booking.controller;

import com.clinic.booking.dto.reception.AdminReceptionRequest;
import com.clinic.booking.dto.common.ApiResponse;
import com.clinic.booking.service.AdminReceptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/admin/reception")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class AdminReceptionController {

    private final AdminReceptionService receptionService;

    @PostMapping
    public ApiResponse<Object> createReception(@Valid @RequestBody AdminReceptionRequest request) {
        return ApiResponse.success(receptionService.createReceptionAndVitals(request));
    }
}
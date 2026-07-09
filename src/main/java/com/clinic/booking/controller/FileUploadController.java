package com.clinic.booking.controller;

import com.clinic.booking.dto.common.ApiResponse;
import com.clinic.booking.service.FileUploadService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/files")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class FileUploadController {

    private final FileUploadService fileUploadService;

    @PostMapping("/upload")
    public ApiResponse<String> uploadFile(@RequestParam("file") MultipartFile file) {
        String fileUrl = fileUploadService.uploadFile(file);
        return ApiResponse.success(fileUrl);
    }
}

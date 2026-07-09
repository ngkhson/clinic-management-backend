package com.clinic.booking.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class FileUploadService {

    // Thư mục lưu trữ file
    private final String UPLOAD_DIR = "uploads";

    public String uploadFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new RuntimeException("File is empty");
        }

        try {
            // Tạo thư mục nếu chưa tồn tại
            File directory = new File(UPLOAD_DIR);
            if (!directory.exists()) {
                directory.mkdirs();
            }

            // Đổi tên file để tránh trùng lặp
            String originalFilename = file.getOriginalFilename();
            String extension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }
            String newFilename = UUID.randomUUID().toString() + extension;

            // Đường dẫn lưu file
            Path filePath = Paths.get(UPLOAD_DIR, newFilename);
            Files.write(filePath, file.getBytes());

            // Trả về đường dẫn truy cập file (sau này map với /uploads/**)
            return "/uploads/" + newFilename;

        } catch (IOException e) {
            throw new RuntimeException("Could not store file", e);
        }
    }
}

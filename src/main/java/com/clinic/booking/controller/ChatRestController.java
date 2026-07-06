package com.clinic.booking.controller;

import com.clinic.booking.dto.chat.ChatHistoryResponse;
import com.clinic.booking.dto.common.ApiResponse;
import com.clinic.booking.dto.chat.ChatRoomResponse;
import com.clinic.booking.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chat")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class ChatRestController {

    private final ChatService chatService;

    // API lấy lịch sử chat
    @GetMapping("/history")
    public ApiResponse<List<ChatHistoryResponse>> getHistory(@RequestParam String patientEmail) {
        return ApiResponse.success(chatService.getHistory(patientEmail));
    }

    // API lấy danh sách người đã chat (dành cho Admin)
    @GetMapping("/rooms")
    public ApiResponse<List<ChatRoomResponse>> getActiveRooms() {
        return ApiResponse.success(chatService.getActiveRooms());
    }
}
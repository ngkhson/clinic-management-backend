package com.clinic.booking.controller;

import com.clinic.booking.dto.ChatHistoryDTO;
import com.clinic.booking.dto.ChatRoomDTO;
import com.clinic.booking.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<List<ChatHistoryDTO>> getHistory(@RequestParam String patientEmail) {
        return ResponseEntity.ok(chatService.getHistory(patientEmail));
    }

    // API lấy danh sách người đã chat (dành cho Admin)
    @GetMapping("/rooms")
    public ResponseEntity<List<ChatRoomDTO>> getActiveRooms() {
        return ResponseEntity.ok(chatService.getActiveRooms());
    }
}
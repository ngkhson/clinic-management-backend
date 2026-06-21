package com.clinic.booking.controller;

import com.clinic.booking.dto.ChatHistoryDTO;
import com.clinic.booking.dto.ChatMessage;
import com.clinic.booking.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class ChatController {

    private final SimpMessagingTemplate messagingTemplate;
    private final ChatService chatService;

    @MessageMapping("/chat.sendPrivateMessage")
    public void sendPrivateMessage(@Payload ChatMessage chatMessage) {

        // 1. Lưu tin nhắn vào DB thông qua ChatService
        ChatHistoryDTO savedMessage = chatService.saveMessage(
                chatMessage.getSenderEmail(),
                chatMessage.getReceiverEmail(),
                chatMessage.getContent()
        );
        chatMessage.setTimestamp(savedMessage.getTimestamp());

        // 2. LOGIC ĐỊNH TUYẾN MỚI (BROADCAST TỚI STAFF)
        if ("STAFF".equals(chatMessage.getReceiverEmail())) {
            // Trường hợp 1: Bệnh nhân gửi tin nhắn cho phòng khám
            // -> Phát tin nhắn lên kênh chung của nhân viên (/topic/staff)
            messagingTemplate.convertAndSend("/topic/staff", chatMessage);
            // -> Gửi lại một bản copy cho bệnh nhân để hiển thị trên màn hình của họ
            messagingTemplate.convertAndSend("/queue/messages/" + chatMessage.getSenderEmail(), chatMessage);
        } else {
            // Trường hợp 2: Nhân viên (Admin/Doctor) trả lời bệnh nhân
            // -> Gửi trực tiếp đến kênh riêng của bệnh nhân đó
            messagingTemplate.convertAndSend("/queue/messages/" + chatMessage.getReceiverEmail(), chatMessage);
            // -> Đồng thời phát lại tin nhắn này lên kênh chung của nhân viên
            // (để các Admin/Doctor khác thấy câu trả lời và biết khách đã được hỗ trợ)
            messagingTemplate.convertAndSend("/topic/staff", chatMessage);
        }
    }
}
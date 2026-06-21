package com.clinic.booking.service;

import com.clinic.booking.dto.ChatHistoryDTO;
import com.clinic.booking.dto.ChatRoomDTO;
import com.clinic.booking.entity.ChatRoom;
import com.clinic.booking.entity.Message;
import com.clinic.booking.entity.User;
import com.clinic.booking.repository.ChatRoomRepository;
import com.clinic.booking.repository.MessageRepository;
import com.clinic.booking.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatRoomRepository chatRoomRepository;
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;

    @Transactional
    public ChatHistoryDTO saveMessage(String senderEmail, String receiverEmail, String content) {
        User sender = userRepository.findByEmail(senderEmail).orElseThrow(() -> new RuntimeException("Sender not found"));

        // Nếu receiver là "STAFF", người gửi chắc chắn là Bệnh nhân.
        // Nếu không, Staff đang trả lời, nên receiver chính là Bệnh nhân.
        User patient;
        if ("STAFF".equals(receiverEmail)) {
            patient = sender;
        } else {
            patient = userRepository.findByEmail(receiverEmail).orElseThrow();
        }

        // Tìm phòng chat, nếu chưa có thì tạo mới
        ChatRoom room = chatRoomRepository.findByUserId(patient.getId())
                .orElseGet(() -> chatRoomRepository.save(ChatRoom.builder().user(patient).status("OPEN").build()));

        // Lưu tin nhắn
        Message message = Message.builder()
                .chatRoom(room)
                .sender(sender)
                .content(content)
                .isRead(false)
                .build();
        message = messageRepository.save(message);

        return ChatHistoryDTO.builder()
                .senderEmail(senderEmail)
                .receiverEmail(receiverEmail)
                .content(content)
                .timestamp(message.getCreatedAt() != null ? message.getCreatedAt().toString() : java.time.LocalDateTime.now().toString())
                .type("CHAT")
                .build();
    }

    // ĐÃ SỬA: Bổ sung trường receiverEmail để React lọc đúng tin nhắn khi load lịch sử
    public List<ChatHistoryDTO> getHistory(String patientEmail) {
        User patient = userRepository.findByEmail(patientEmail).orElseThrow();
        return chatRoomRepository.findByUserId(patient.getId())
                .map(room -> messageRepository.findByChatRoomIdOrderByCreatedAtAsc(room.getId())
                        .stream()
                        .map(msg -> {
                            // Kiểm tra xem ai là người gửi (Bệnh nhân hay là Nhân viên)
                            boolean isPatientSender = msg.getSender().getId().equals(room.getUser().getId());
                            return ChatHistoryDTO.builder()
                                    .senderEmail(msg.getSender().getEmail())
                                    // Nếu bệnh nhân gửi thì người nhận là STAFF, ngược lại người nhận là Bệnh nhân
                                    .receiverEmail(isPatientSender ? "STAFF" : room.getUser().getEmail())
                                    .content(msg.getContent())
                                    .timestamp(msg.getCreatedAt() != null ? msg.getCreatedAt().toString() : "")
                                    .type("CHAT")
                                    .build();
                        })
                        .collect(Collectors.toList()))
                .orElse(List.of());
    }

    // Lấy danh sách các phòng chat (Dành cho Admin)
    public List<ChatRoomDTO> getActiveRooms() {
        return chatRoomRepository.findAll().stream()
                .map(room -> {
                    List<Message> msgs = messageRepository.findByChatRoomIdOrderByCreatedAtAsc(room.getId());
                    String lastMsg = msgs.isEmpty() ? "Chưa có tin nhắn" : msgs.get(msgs.size() - 1).getContent();
                    return ChatRoomDTO.builder()
                            .patientEmail(room.getUser().getEmail())
                            .patientName(room.getUser().getFullName())
                            .lastMessage(lastMsg)
                            .build();
                })
                .collect(Collectors.toList());
    }
}
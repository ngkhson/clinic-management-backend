package com.clinic.booking.service;

import com.clinic.booking.exception.AppException;
import com.clinic.booking.exception.ErrorCode;

import com.clinic.booking.dto.chat.ChatHistoryResponse;
import com.clinic.booking.dto.chat.ChatRoomResponse;
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
    public ChatHistoryResponse saveMessage(String senderEmail, String receiverEmail, String content) {
        User sender = userRepository.findByEmail(senderEmail).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

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

        return ChatHistoryResponse.builder()
                .senderEmail(senderEmail)
                .receiverEmail(receiverEmail)
                .content(content)
                .timestamp(message.getCreatedAt() != null ? message.getCreatedAt().toString() : java.time.LocalDateTime.now().toString())
                .type("CHAT")
                .build();
    }

    // ĐÃ SỬA: Bổ sung trường receiverEmail để React lọc đúng tin nhắn khi load lịch sử
    public List<ChatHistoryResponse> getHistory(String patientEmail, String currentUserEmail) {
        User patient = userRepository.findByEmail(patientEmail).orElseThrow();
        return chatRoomRepository.findByUserId(patient.getId())
                .map(room -> {
                    List<Message> messages = messageRepository.findByChatRoomIdOrderByCreatedAtAsc(room.getId());
                    boolean isStaff = !currentUserEmail.equals(patientEmail);
                    
                    List<ChatHistoryResponse> responses = messages.stream()
                        .map(msg -> {
                            boolean isPatientSender = msg.getSender().getId().equals(room.getUser().getId());
                            
                            // Tự động đánh dấu đã đọc nếu người đang xem không phải là người gửi
                            if (isStaff && isPatientSender && !msg.getIsRead()) {
                                msg.setIsRead(true);
                                messageRepository.save(msg);
                            } else if (!isStaff && !isPatientSender && !msg.getIsRead()) {
                                msg.setIsRead(true);
                                messageRepository.save(msg);
                            }
                            
                            return ChatHistoryResponse.builder()
                                    .senderEmail(msg.getSender().getEmail())
                                    .receiverEmail(isPatientSender ? "STAFF" : room.getUser().getEmail())
                                    .content(msg.getContent())
                                    .timestamp(msg.getCreatedAt() != null ? msg.getCreatedAt().toString() : "")
                                    .type("CHAT")
                                    .build();
                        })
                        .collect(Collectors.toList());
                    
                    return responses;
                })
                .orElse(List.of());
    }

    // Lấy danh sách các phòng chat (Dành cho Admin)
    public List<ChatRoomResponse> getActiveRooms() {
        return chatRoomRepository.findAll().stream()
                .map(room -> {
                    List<Message> msgs = messageRepository.findByChatRoomIdOrderByCreatedAtAsc(room.getId());
                    String lastMsg = msgs.isEmpty() ? "Chưa có tin nhắn" : msgs.get(msgs.size() - 1).getContent();
                    
                    // Admin/Doctor xem danh sách phòng thì chỉ đếm những tin nhắn do bệnh nhân gửi mà chưa đọc
                    long unreadCount = msgs.stream()
                        .filter(msg -> msg.getSender().getId().equals(room.getUser().getId()) && !msg.getIsRead())
                        .count();
                        
                    return ChatRoomResponse.builder()
                            .patientEmail(room.getUser().getEmail())
                            .patientName(room.getUser().getFullName())
                            .lastMessage(lastMsg)
                            .unreadCount((int) unreadCount)
                            .build();
                })
                .collect(Collectors.toList());
    }

    public int getUnreadCount(String currentUserEmail) {
        User user = userRepository.findByEmail(currentUserEmail).orElseThrow();
        
        boolean isStaff = user.getRoles().stream()
                .anyMatch(role -> role.getName().equals("ADMIN") || role.getName().equals("DOCTOR"));

        if (isStaff) {
            // Staff unread count is calculated across all rooms
            return chatRoomRepository.findAll().stream()
                .mapToInt(room -> (int) messageRepository.findByChatRoomIdOrderByCreatedAtAsc(room.getId()).stream()
                    .filter(msg -> msg.getSender().getId().equals(room.getUser().getId()) && !msg.getIsRead())
                    .count())
                .sum();
        } else {
            // Patient unread count is calculated from their single room
            return chatRoomRepository.findByUserId(user.getId())
                .map(room -> (int) messageRepository.findByChatRoomIdOrderByCreatedAtAsc(room.getId()).stream()
                    .filter(msg -> !msg.getSender().getId().equals(room.getUser().getId()) && !msg.getIsRead())
                    .count())
                .orElse(0);
        }
    }
}
package com.clinic.booking.service;

import com.clinic.booking.dto.NotificationDTO;
import com.clinic.booking.entity.Notification;
import com.clinic.booking.entity.User;
import com.clinic.booking.repository.NotificationRepository;
import com.clinic.booking.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final SimpMessagingTemplate messagingTemplate;

    // Hàm tiện ích để các Service khác gọi khi muốn đẩy thông báo
    @Transactional
    public void sendNotification(User user, String message) {
        Notification notif = Notification.builder()
                .user(user)
                .message(message)
                .isRead(false)
                .createdAt(LocalDateTime.now())
                .build();
        notif = notificationRepository.save(notif);

        // Bắn qua WebSocket để Real-time (tương lai Frontend có thể bắt)
        messagingTemplate.convertAndSend("/queue/notifications/" + user.getEmail(), mapToDTO(notif));
    }

    public List<NotificationDTO> getMyNotifications() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email).orElseThrow();
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(user.getId())
                .stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    @Transactional
    public void markAsRead(Long id) {
        Notification notif = notificationRepository.findById(id).orElseThrow();
        notif.setRead(true);
        notificationRepository.save(notif);
    }

    @Transactional
    public void markAllAsRead() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email).orElseThrow();
        List<Notification> notifs = notificationRepository.findByUserIdAndIsReadFalse(user.getId());
        notifs.forEach(n -> n.setRead(true));
        notificationRepository.saveAll(notifs);
    }

    private NotificationDTO mapToDTO(Notification n) {
        return NotificationDTO.builder()
                .id(n.getId())
                .message(n.getMessage())
                .isRead(n.isRead())
                .createdAt(n.getCreatedAt().toString())
                .build();
    }
}
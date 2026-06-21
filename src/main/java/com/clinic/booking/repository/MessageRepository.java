package com.clinic.booking.repository;

import com.clinic.booking.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
    // Lấy toàn bộ tin nhắn của một phòng chat, sắp xếp theo thời gian cũ -> mới
    List<Message> findByChatRoomIdOrderByCreatedAtAsc(Long roomId);
}
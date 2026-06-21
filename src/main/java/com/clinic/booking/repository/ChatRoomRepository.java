//package com.clinic.booking.repository;
//
//import com.clinic.booking.entity.ChatRoom;
//import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.stereotype.Repository;
//
//import java.util.List;
//import java.util.Optional;
//
//@Repository
//public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {
//
//    // Tìm phòng chat của một user với trạng thái cụ thể (VD: tìm phòng chat đang 'OPEN' của Bệnh nhân A)
//    Optional<ChatRoom> findByUserIdAndStatus(Long userId, String status);
//
//    // Dành cho Admin: Lấy danh sách tất cả các phòng chat đang chờ hỗ trợ
//    List<ChatRoom> findByStatus(String status);
//}

package com.clinic.booking.repository;

import com.clinic.booking.entity.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {
    // Tìm phòng chat dựa trên ID của Bệnh nhân
    Optional<ChatRoom> findByUserId(Long userId);
}
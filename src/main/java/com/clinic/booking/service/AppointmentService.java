package com.clinic.booking.service;

import com.clinic.booking.dto.AppointmentDTO;
import com.clinic.booking.entity.Appointment;
import com.clinic.booking.entity.Doctor;
import com.clinic.booking.entity.Schedule;
import com.clinic.booking.entity.User;
import com.clinic.booking.repository.AppointmentRepository;
import com.clinic.booking.repository.DoctorRepository;
import com.clinic.booking.repository.ScheduleRepository;
import com.clinic.booking.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final ScheduleRepository scheduleRepository;
    private final DoctorRepository doctorRepository;
    private final UserRepository userRepository;

    // Annotation @Transactional cực kỳ quan trọng:
    // Đảm bảo nếu bị lỗi giữa chừng (ví dụ lưu Appointment thành công nhưng cập nhật Schedule thất bại),
    // toàn bộ giao dịch sẽ bị rollback (hủy bỏ) để không làm sai lệch dữ liệu.
    @Transactional
    public AppointmentDTO createAppointment(AppointmentDTO request) {
        // 1. Lấy thông tin User đang đăng nhập (nhờ JwtAuthenticationFilter)
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        User patient = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng!"));

        // 2. Kiểm tra Bác sĩ và Lịch khám
        Doctor doctor = doctorRepository.findById(request.getDoctorId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy Bác sĩ!"));

        Schedule schedule = scheduleRepository.findById(request.getScheduleId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy Lịch khám!"));

        // 3. Kiểm tra logic kinh doanh: Lịch khám đã đầy chưa?
        if (schedule.getCurrentPatients() >= schedule.getMaxPatients()) {
            throw new RuntimeException("Xin lỗi, ca khám này đã nhận đủ số lượng bệnh nhân!");
        }

        // 4. Tạo Lịch hẹn mới
        Appointment appointment = Appointment.builder()
                .patient(patient)
                .doctor(doctor)
                .schedule(schedule)
                .appointmentDate(schedule.getWorkDate())
                .status("PENDING") // Trạng thái ban đầu
                .symptoms(request.getSymptoms())
                .build();

        appointment = appointmentRepository.save(appointment);

        // 5. Cập nhật tăng số lượng bệnh nhân trong ca đó
        schedule.setCurrentPatients(schedule.getCurrentPatients() + 1);
        scheduleRepository.save(schedule);

        // 6. Trả về kết quả
        return mapToDTO(appointment);
    }

    private AppointmentDTO mapToDTO(Appointment appointment) {
        return AppointmentDTO.builder()
                .id(appointment.getId())
                .doctorId(appointment.getDoctor().getId())
                .doctorName(appointment.getDoctor().getUser().getFullName())
                .scheduleId(appointment.getSchedule().getId())
                .timeSlot(appointment.getSchedule().getTimeSlot())
                .appointmentDate(appointment.getAppointmentDate())
                .status(appointment.getStatus())
                .symptoms(appointment.getSymptoms())
                .createdAt(appointment.getCreatedAt())
                .build();
    }
}
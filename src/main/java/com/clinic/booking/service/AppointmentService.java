package com.clinic.booking.service;

import com.clinic.booking.exception.AppException;
import com.clinic.booking.exception.ErrorCode;

import com.clinic.booking.dto.appointment.AppointmentRequest;
import com.clinic.booking.dto.appointment.AppointmentResponse;
import com.clinic.booking.entity.Appointment;
import com.clinic.booking.entity.Doctor;
import com.clinic.booking.entity.Schedule;
import com.clinic.booking.entity.User;
import com.clinic.booking.repository.AppointmentRepository;
import com.clinic.booking.repository.DoctorRepository;
import com.clinic.booking.repository.ScheduleRepository;
import com.clinic.booking.repository.UserRepository;
import com.clinic.booking.dto.notification.EmailMessage;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final ScheduleRepository scheduleRepository;
    private final DoctorRepository doctorRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;
    private final EmailProducer emailProducer;
    private final RedissonClient redissonClient;

    @Transactional
    public AppointmentResponse createAppointment(AppointmentRequest request) {
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        User patient = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        // Kiểm tra không cho đặt lịch trước giờ hiện tại trừ đi 15 phút
        String startTimeStr = request.getTimeSlot().split(" - ")[0].trim();
        LocalTime startTime = LocalTime.parse(startTimeStr);
        LocalDateTime appointmentDateTime = LocalDateTime.of(request.getAppointmentDate(), startTime);
        
        if (appointmentDateTime.isBefore(LocalDateTime.now().minusMinutes(15))) {
            throw new AppException(ErrorCode.INVALID_APPOINTMENT_TIME);
        }

        // --- DISTRIBUTED LOCK VỚI REDIS ---
        // Tạo key đặc trưng cho khung giờ khám của chuyên khoa này
        String lockKey = String.format("booking:lock:specialty:%d:date:%s:time:%s", 
                                       request.getSpecialtyId(), 
                                       request.getAppointmentDate(), 
                                       request.getTimeSlot());
        
        RLock lock = redissonClient.getLock(lockKey);
        
        try {
            // Cố gắng lấy khóa, đợi tối đa 3 giây, nếu lấy được thì khóa sẽ tự nhả sau 10 giây
            boolean isLocked = lock.tryLock(3, 10, TimeUnit.SECONDS);
            if (!isLocked) {
                // Có quá nhiều người đang cố đặt cùng lúc và không lấy được khóa
                throw new AppException(ErrorCode.SCHEDULE_FULL); // Có thể tạo ErrorCode.SYSTEM_BUSY
            }

            // Đã lấy được khóa -> Tiến hành check DB an toàn
            List<Schedule> availableSchedules = scheduleRepository.findAvailableSchedules(
                    request.getSpecialtyId(), request.getAppointmentDate(), request.getTimeSlot());

            if (availableSchedules.isEmpty()) {
                throw new AppException(ErrorCode.SCHEDULE_FULL);
            }

            Schedule schedule = availableSchedules.get(0);
            Doctor doctor = schedule.getDoctor();

            Appointment appointment = Appointment.builder()
                    .patient(patient)
                    .doctor(doctor)
                    .schedule(schedule)
                    .appointmentDate(schedule.getWorkDate())
                    .status("PENDING")
                    .symptoms(request.getSymptoms())
                    .build();

            appointment = appointmentRepository.save(appointment);

            schedule.setCurrentPatients(schedule.getCurrentPatients() + 1);
            scheduleRepository.save(schedule);

            notificationService.sendNotification(patient, "Đặt lịch khám thành công. Lịch hẹn của bạn đang chờ xác nhận từ Lễ tân.");

            // --- GỬI EMAIL BẤT ĐỒNG BỘ QUA RABBITMQ ---
            EmailMessage emailMessage = EmailMessage.builder()
                    .toEmail(patient.getEmail())
                    .subject("Xác nhận Đặt Lịch Khám Thành Công - MediPro")
                    .body("Kính chào " + patient.getFullName() + ",\n\n"
                            + "Lịch khám của bạn đã được ghi nhận trên hệ thống.\n"
                            + "Thông tin chi tiết:\n"
                            + "- Bác sĩ: " + doctor.getUser().getFullName() + "\n"
                            + "- Ngày khám: " + schedule.getWorkDate() + "\n"
                            + "- Khung giờ: " + schedule.getTimeSlot() + "\n\n"
                            + "Vui lòng đến đúng giờ. Cảm ơn bạn đã tin tưởng phòng khám MediPro!")
                    .build();
            emailProducer.sendEmailMessage(emailMessage);

            return mapToDTO(appointment);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Có lỗi xảy ra khi lấy khóa Redis", e);
        } finally {
            // Quan trọng: Luôn nhả khóa sau khi xử lý xong (kể cả khi thành công hay bị lỗi)
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

    private AppointmentResponse mapToDTO(Appointment appointment) {
        return AppointmentResponse.builder()
                .id(appointment.getId())
                .doctorId(appointment.getDoctor().getId())
                .doctorName(appointment.getDoctor().getUser().getFullName())
                .patientName(appointment.getPatient().getFullName())
                .scheduleId(appointment.getSchedule().getId())
                .timeSlot(appointment.getSchedule().getTimeSlot())
                .appointmentDate(appointment.getAppointmentDate())
                .status(appointment.getStatus())
                .symptoms(appointment.getSymptoms())
                .createdAt(appointment.getCreatedAt())
                .build();
    }

    @Transactional
    public AppointmentResponse updateAppointment(Long id, com.clinic.booking.dto.appointment.AppointmentUpdateRequest request) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.APPOINTMENT_NOT_FOUND));

        boolean wasCancelled = "CANCELLED".equals(appointment.getStatus()) || "NO_SHOW".equals(appointment.getStatus());
        boolean isNowCancelled = "CANCELLED".equals(request.getStatus()) || "NO_SHOW".equals(request.getStatus());

        if (request.getStatus() != null && !request.getStatus().equals(appointment.getStatus())) {
            appointment.setStatus(request.getStatus());
            
            // Nếu chuyển từ trạng thái bình thường sang CANCELLED/NO_SHOW -> Giảm currentPatients
            if (!wasCancelled && isNowCancelled) {
                Schedule schedule = appointment.getSchedule();
                if (schedule.getCurrentPatients() > 0) {
                    schedule.setCurrentPatients(schedule.getCurrentPatients() - 1);
                    scheduleRepository.save(schedule);
                }
            }
            // (Tuỳ chọn: Nếu từ CANCELLED khôi phục lại thì có thể phải cộng lên lại, nhưng thường người ta sẽ tạo mới hẹn)
        }

        if (request.getSymptoms() != null) {
            appointment.setSymptoms(request.getSymptoms());
        }

        appointmentRepository.save(appointment);
        return mapToDTO(appointment);
    }

    @Transactional
    public void cancelAppointment(Long id) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.APPOINTMENT_NOT_FOUND));

        if (!"CANCELLED".equals(appointment.getStatus()) && !"NO_SHOW".equals(appointment.getStatus())) {
            appointment.setStatus("CANCELLED");
            
            Schedule schedule = appointment.getSchedule();
            if (schedule.getCurrentPatients() > 0) {
                schedule.setCurrentPatients(schedule.getCurrentPatients() - 1);
                scheduleRepository.save(schedule);
            }
            appointmentRepository.save(appointment);

            notificationService.sendNotification(
                    appointment.getPatient(),
                    "Bạn đã huỷ thành công lịch hẹn ngày " + appointment.getAppointmentDate().toString() + "."
            );
        }
    }
}
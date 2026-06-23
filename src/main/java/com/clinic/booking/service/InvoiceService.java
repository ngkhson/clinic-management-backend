package com.clinic.booking.service;

import com.clinic.booking.dto.InvoiceDTO;
import com.clinic.booking.entity.*;
import com.clinic.booking.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InvoiceService {

    private final InvoiceRepository invoiceRepository;
    private final AppointmentRepository appointmentRepository;
    private final MedicalRecordRepository medicalRecordRepository;

    @Transactional
    public InvoiceDTO generateInvoice(Long appointmentId) {
        // 1. Kiểm tra xem ca khám này đã tạo hóa đơn chưa. Nếu có rồi thì trả về luôn, không tính lại.
        if (invoiceRepository.findByAppointmentId(appointmentId).isPresent()) {
            return mapToDTO(invoiceRepository.findByAppointmentId(appointmentId).get());
        }

        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy Lịch hẹn!"));

        // 2. Tính Tiền Khám (Lấy từ bảng Doctor)
        double consultationFee = appointment.getDoctor().getExaminationPrice() != null
                ? appointment.getDoctor().getExaminationPrice().doubleValue() : 0.0;

        double serviceFee = 0.0;
        double medicineFee = 0.0;

        // 3. Lấy Bệnh án để soi xem Bác sĩ đã cho làm Dịch vụ gì và Uống thuốc gì
        var recordOpt = medicalRecordRepository.findByAppointmentId(appointmentId);
        if (recordOpt.isPresent()) {
            MedicalRecord record = recordOpt.get();

            // Cộng tiền dịch vụ cận lâm sàng
            if (record.getServices() != null) {
                for (MedicalService s : record.getServices()) {
                    serviceFee += (s.getPrice() != null ? s.getPrice() : 0.0);
                }
            }

            // Cộng tiền thuốc (lấy Giá bán lẻ * Số lượng)
            if (record.getPrescriptionDetails() != null) {
                for (PrescriptionDetail pd : record.getPrescriptionDetails()) {
                    double price = pd.getMedicine().getSellingPrice() != null ? pd.getMedicine().getSellingPrice() : 0.0;
                    medicineFee += (price * pd.getQuantity());
                }
            }
        }

        double totalAmount = consultationFee + serviceFee + medicineFee;

        // 4. Lưu Hóa đơn vào DB
        Invoice invoice = Invoice.builder()
                .appointment(appointment)
                .consultationFee(consultationFee)
                .serviceFee(serviceFee)
                .medicineFee(medicineFee)
                .totalAmount(totalAmount)
                .status("UNPAID")
                .build();

        invoice = invoiceRepository.save(invoice);
        return mapToDTO(invoice);
    }

    @Transactional
    public InvoiceDTO payInvoice(Long invoiceId, String paymentMethod) {
        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy Hóa đơn!"));

        if ("PAID".equals(invoice.getStatus())) {
            throw new RuntimeException("Hóa đơn này đã được thanh toán rồi!");
        }

        invoice.setStatus("PAID");
        invoice.setPaymentMethod(paymentMethod);
        invoice.setPaidAt(LocalDateTime.now());

        invoice = invoiceRepository.save(invoice);
        return mapToDTO(invoice);
    }

    public List<InvoiceDTO> getAllInvoices() {
        return invoiceRepository.findAllByOrderByCreatedAtDesc()
                .stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    private InvoiceDTO mapToDTO(Invoice invoice) {
        return InvoiceDTO.builder()
                .id(invoice.getId())
                .appointmentId(invoice.getAppointment().getId())
                .patientName(invoice.getAppointment().getPatient().getFullName())
                .doctorName(invoice.getAppointment().getDoctor().getUser().getFullName())
                .consultationFee(invoice.getConsultationFee())
                .serviceFee(invoice.getServiceFee())
                .medicineFee(invoice.getMedicineFee())
                .totalAmount(invoice.getTotalAmount())
                .status(invoice.getStatus())
                .paymentMethod(invoice.getPaymentMethod())
                .createdAt(invoice.getCreatedAt() != null ? invoice.getCreatedAt().toString() : null)
                .paidAt(invoice.getPaidAt() != null ? invoice.getPaidAt().toString() : null)
                .build();
    }
}
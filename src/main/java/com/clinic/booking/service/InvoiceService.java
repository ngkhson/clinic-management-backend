package com.clinic.booking.service;

import com.clinic.booking.exception.AppException;
import com.clinic.booking.exception.ErrorCode;

import com.clinic.booking.dto.invoice.InvoiceResponse;
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
    public InvoiceResponse generateInvoice(Long appointmentId) {
        // 1. Kiểm tra xem ca khám này đã tạo hóa đơn chưa. Nếu có rồi thì trả về luôn, không tính lại.
        if (invoiceRepository.findByAppointmentId(appointmentId).isPresent()) {
            return mapToDTO(invoiceRepository.findByAppointmentId(appointmentId).get());
        }

        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new AppException(ErrorCode.APPOINTMENT_NOT_FOUND));


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

        double totalAmount = serviceFee + medicineFee;

        // 4. Lưu Hóa đơn vào DB
        Invoice invoice = Invoice.builder()
                .appointment(appointment)
                .type("MEDICAL")
                .serviceFee(serviceFee)
                .medicineFee(medicineFee)
                .totalAmount(totalAmount)
                .status("UNPAID")
                .build();

        invoice = invoiceRepository.save(invoice);
        return mapToDTO(invoice);
    }

    @Transactional
    public InvoiceResponse payInvoice(Long invoiceId, String paymentMethod) {
        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new AppException(ErrorCode.INVALID_ACTION));

        if ("PAID".equals(invoice.getStatus())) {
            throw new AppException(ErrorCode.INVALID_ACTION);
        }

        invoice.setStatus("PAID");
        invoice.setPaymentMethod(paymentMethod);
        invoice.setPaidAt(LocalDateTime.now());

        invoice = invoiceRepository.save(invoice);
        return mapToDTO(invoice);
    }

    public List<InvoiceResponse> getAllInvoices() {
        return invoiceRepository.findAllByOrderByCreatedAtDesc()
                .stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    public InvoiceResponse getInvoiceByAppointmentId(Long appointmentId) {
        return invoiceRepository.findByAppointmentId(appointmentId)
                .map(this::mapToDTO)
                .orElse(null);
    }

    public org.springframework.data.domain.Page<InvoiceResponse> getInvoiceHistory(
            int page, int size, String search, String type, String paymentMethod) {
        
        org.springframework.data.domain.Pageable pageable = 
                org.springframework.data.domain.PageRequest.of(page, size, 
                        org.springframework.data.domain.Sort.by("id").descending());
        
        String searchParam = (search == null || search.trim().isEmpty()) ? null : search.trim();
        String typeParam = (type == null || type.equals("ALL")) ? null : type;
        String methodParam = (paymentMethod == null || paymentMethod.equals("ALL")) ? null : paymentMethod;
        
        return invoiceRepository.findInvoiceHistory("PAID", typeParam, methodParam, searchParam, pageable)
                .map(this::mapToDTO);
    }

    private InvoiceResponse mapToDTO(Invoice invoice) {
        boolean isRetail = "RETAIL".equals(invoice.getType());
        
        return InvoiceResponse.builder()
                .id(invoice.getId())
                .type(invoice.getType() != null ? invoice.getType() : "MEDICAL")
                .appointmentId(isRetail ? null : invoice.getAppointment().getId())
                .patientName(isRetail ? invoice.getCustomerName() : invoice.getAppointment().getPatient().getFullName())
                .doctorName(isRetail ? "Bán lẻ tại quầy" : invoice.getAppointment().getDoctor().getUser().getFullName())
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
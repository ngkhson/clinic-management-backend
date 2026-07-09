package com.clinic.booking.service;

import com.clinic.booking.entity.MedicalRecord;
import com.clinic.booking.entity.PrescriptionDetail;
import com.clinic.booking.entity.MedicalService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.text.NumberFormat;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    @Async
    public void sendMedicalRecordEmail(String toEmail, MedicalRecord record, String patientName) {
        if (toEmail == null || toEmail.trim().isEmpty()) {
            return;
        }

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(toEmail);
            helper.setSubject("Hồ sơ bệnh án & Kết quả khám bệnh - Hệ thống Y tế MediPro");
            helper.setFrom("mediproadmin@gmail.com");

            String htmlContent = buildHtmlContent(record, patientName);
            helper.setText(htmlContent, true);

            mailSender.send(message);
        } catch (MessagingException e) {
            System.err.println("Lỗi khi gửi email bệnh án cho " + toEmail + ": " + e.getMessage());
        }
    }

    private String buildHtmlContent(MedicalRecord record, String patientName) {
        StringBuilder html = new StringBuilder();
        html.append("<html><body style='font-family: Arial, sans-serif; line-height: 1.6; color: #333;'>");
        html.append("<h2 style='color: #1e3a8a;'>Hệ thống Y tế MediPro</h2>");
        html.append("<h3>Xin chào ").append(patientName).append(",</h3>");
        html.append("<p>Cảm ơn bạn đã tin tưởng và sử dụng dịch vụ khám chữa bệnh tại MediPro.</p>");
        html.append("<p>Đây là kết quả khám bệnh của bạn vào ngày <b>")
            .append(record.getAppointment().getAppointmentDate().toString()).append("</b>:</p>");

        html.append("<hr style='border: 1px solid #eee; margin: 20px 0;'>");

        // Sinh hiệu
        html.append("<h4>I. Chỉ số sinh hiệu</h4>");
        html.append("<ul>");
        if (record.getPulse() != null) html.append("<li>Nhịp tim: ").append(record.getPulse()).append(" bpm</li>");
        if (record.getTemp() != null) html.append("<li>Nhiệt độ: ").append(record.getTemp()).append(" &deg;C</li>");
        if (record.getBp() != null) html.append("<li>Huyết áp: ").append(record.getBp()).append(" mmHg</li>");
        if (record.getWeight() != null) html.append("<li>Cân nặng: ").append(record.getWeight()).append(" kg</li>");
        html.append("</ul>");

        // Chẩn đoán
        html.append("<h4>II. Chẩn đoán</h4>");
        html.append("<p><b>Lý do khám:</b> ").append(record.getReasonForVisit() != null ? record.getReasonForVisit() : "").append("</p>");
        html.append("<p><b>Chẩn đoán:</b> ").append(record.getDiagnosis() != null ? record.getDiagnosis() : "").append("</p>");
        html.append("<p><b>Hướng điều trị:</b> ").append(record.getTreatmentPlan() != null ? record.getTreatmentPlan() : "").append("</p>");
        html.append("<p><b>Lời dặn:</b> ").append(record.getNotes() != null ? record.getNotes() : "").append("</p>");

        // Dịch vụ cận lâm sàng
        if (record.getServices() != null && !record.getServices().isEmpty()) {
            html.append("<h4>III. Dịch vụ cận lâm sàng đã chỉ định</h4>");
            html.append("<ul>");
            for (MedicalService s : record.getServices()) {
                html.append("<li>").append(s.getName()).append("</li>");
            }
            html.append("</ul>");
        }

        // Đơn thuốc
        if (record.getPrescriptionDetails() != null && !record.getPrescriptionDetails().isEmpty()) {
            html.append("<h4>IV. Đơn thuốc</h4>");
            html.append("<table style='width: 100%; border-collapse: collapse; border: 1px solid #ddd;'>");
            html.append("<tr style='background-color: #f9f9f9;'>");
            html.append("<th style='border: 1px solid #ddd; padding: 8px;'>Tên thuốc</th>");
            html.append("<th style='border: 1px solid #ddd; padding: 8px;'>Số lượng</th>");
            html.append("<th style='border: 1px solid #ddd; padding: 8px;'>Hướng dẫn</th>");
            html.append("</tr>");
            for (PrescriptionDetail pd : record.getPrescriptionDetails()) {
                html.append("<tr>");
                html.append("<td style='border: 1px solid #ddd; padding: 8px;'>").append(pd.getMedicine().getName()).append("</td>");
                html.append("<td style='border: 1px solid #ddd; padding: 8px;'>").append(pd.getQuantity()).append(" ").append(pd.getMedicine().getUnit()).append("</td>");
                html.append("<td style='border: 1px solid #ddd; padding: 8px;'>").append(pd.getDosageInstruction() != null ? pd.getDosageInstruction() : "").append("</td>");
                html.append("</tr>");
            }
            html.append("</table>");
        }

        // Kết quả cận lâm sàng
        if (record.getParaclinicalResults() != null && !record.getParaclinicalResults().trim().isEmpty()) {
            html.append("<h4>V. Kết quả cận lâm sàng</h4>");
            String paraclinicalText = record.getParaclinicalResults().replace("\n", "<br>");
            // Bỏ tính năng hiển thị ảnh, thay bằng text thông báo
            paraclinicalText = paraclinicalText.replaceAll("\\[IMAGE:(.*?)\\]", 
                    "<div style='margin: 10px 0; color: #666; font-style: italic;'>[Hình ảnh đã được ẩn khỏi email, vui lòng xem trên hệ thống]</div>");
            html.append("<div style='background-color: #f9f9f9; padding: 15px; border: 1px solid #ddd; border-radius: 5px;'>")
                .append(paraclinicalText)
                .append("</div>");
        }

        html.append("<br><p>Bạn có thể đăng nhập vào hệ thống để xem chi tiết hoặc tải xuống hóa đơn và kết quả hình ảnh cận lâm sàng.</p>");
        html.append("<p>Trân trọng,<br>Hệ thống Y tế MediPro</p>");
        html.append("</body></html>");
        
        return html.toString();
    }
}

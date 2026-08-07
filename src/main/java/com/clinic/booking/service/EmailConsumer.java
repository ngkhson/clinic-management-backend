package com.clinic.booking.service;

import com.clinic.booking.config.RabbitMQConfig;
import com.clinic.booking.dto.notification.EmailMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailConsumer {

    private final JavaMailSender mailSender;

    // Hàm này sẽ tự động chạy ngầm mỗi khi có tin nhắn mới trong Queue
    @RabbitListener(queues = RabbitMQConfig.QUEUE_NAME)
    public void consumeEmailMessage(EmailMessage emailMessage) {
        try {
            System.out.println("Consumer đang xử lý gửi Email tới: " + emailMessage.getToEmail());

            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(emailMessage.getToEmail());
            message.setSubject(emailMessage.getSubject());
            message.setText(emailMessage.getBody());

            mailSender.send(message);
            
            System.out.println("Gửi Email thành công tới: " + emailMessage.getToEmail());
        } catch (Exception e) {
            System.err.println("Lỗi khi gửi email qua RabbitMQ: " + e.getMessage());
            // Trong thực tế, có thể cấu hình Dead Letter Queue (DLQ) để lưu lại các tin nhắn bị lỗi
        }
    }
}

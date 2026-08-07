package com.clinic.booking.service;

import com.clinic.booking.config.RabbitMQConfig;
import com.clinic.booking.dto.notification.EmailMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailProducer {

    private final RabbitTemplate rabbitTemplate;

    public void sendEmailMessage(EmailMessage emailMessage) {
        // Đẩy tin nhắn vào Sàn giao dịch (Exchange) với Routing Key tương ứng
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.EXCHANGE_NAME,
                RabbitMQConfig.ROUTING_KEY,
                emailMessage
        );
        System.out.println("Đã đẩy yêu cầu gửi Email vào RabbitMQ: " + emailMessage.getToEmail());
    }
}

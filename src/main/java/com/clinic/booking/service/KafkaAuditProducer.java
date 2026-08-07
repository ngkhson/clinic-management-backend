package com.clinic.booking.service;

import com.clinic.booking.config.KafkaConfig;
import com.clinic.booking.dto.notification.AuditEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KafkaAuditProducer {

    private final KafkaTemplate kafkaTemplate;

    public void sendAuditLog(AuditEvent event) {
        kafkaTemplate.send(KafkaConfig.AUDIT_LOG_TOPIC, event);
        System.out.println("Đã bắn AuditEvent vào Kafka: " + event.getAction() + " - " + event.getEntityName());
    }
}

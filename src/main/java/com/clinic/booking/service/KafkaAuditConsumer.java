package com.clinic.booking.service;

import com.clinic.booking.config.KafkaConfig;
import com.clinic.booking.dto.notification.AuditEvent;
import com.clinic.booking.entity.AuditLog;
import com.clinic.booking.repository.AuditLogMongoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KafkaAuditConsumer {

    private final AuditLogMongoRepository auditLogMongoRepository;

    @KafkaListener(topics = KafkaConfig.AUDIT_LOG_TOPIC, groupId = "audit-log-group")
    public void consumeAuditLog(AuditEvent event) {
        System.out.println("Consumer đang xử lý AuditEvent và lưu vào MongoDB: " + event.getAction());

        AuditLog log = AuditLog.builder()
                .userEmail(event.getUserEmail())
                .action(event.getAction())
                .entityName(event.getEntityName())
                .details(event.getDetails())
                .timestamp(event.getTimestamp())
                .build();

        auditLogMongoRepository.save(log);
        System.out.println("Lưu thành công Log vào MongoDB.");
    }
}

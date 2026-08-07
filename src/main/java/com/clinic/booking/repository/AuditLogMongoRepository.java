package com.clinic.booking.repository;

import com.clinic.booking.entity.AuditLog;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuditLogMongoRepository extends MongoRepository<AuditLog, String> {
}

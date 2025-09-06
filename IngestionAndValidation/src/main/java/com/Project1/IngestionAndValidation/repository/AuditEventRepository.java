package com.Project1.IngestionAndValidation.repository;

import com.Project1.IngestionAndValidation.Models.AuditEvent;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface AuditEventRepository extends MongoRepository<AuditEvent, String> {
    boolean existsByMessageId(String messageId);
}

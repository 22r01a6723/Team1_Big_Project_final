package com.complyvault.retention.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.complyvault.retention.model.RetentionAuditLog;

@Repository
public interface RetentionAuditLogRepository extends MongoRepository<RetentionAuditLog, String> {
    // No extra methods needed for now; basic save() is enough
}

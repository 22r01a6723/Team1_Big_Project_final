package com.project_1.normalizer.repository;


import com.project_1.normalizer.model.AuditEvent;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface AuditRepository extends MongoRepository<AuditEvent, String> {
}

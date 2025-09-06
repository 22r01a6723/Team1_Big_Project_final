package com.smarsh.compliance.mongodb;

import com.smarsh.compliance.models.AuditEvent;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuditRepository extends MongoRepository<AuditEvent, String> {
}

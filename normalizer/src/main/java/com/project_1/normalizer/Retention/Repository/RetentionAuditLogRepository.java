package com.project_1.normalizer.Retention.Repository;

import com.project_1.normalizer.Retention.model.RetentionAuditLog;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RetentionAuditLogRepository extends MongoRepository<RetentionAuditLog, String> {
    // No extra methods needed for now; basic save() is enough
}

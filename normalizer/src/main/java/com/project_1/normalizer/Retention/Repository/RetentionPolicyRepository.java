package com.project_1.normalizer.Retention.Repository;

import com.project_1.normalizer.Retention.model.RetentionPolicy;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.List;

@Repository
public interface RetentionPolicyRepository extends MongoRepository<RetentionPolicy, String> {
    Optional<RetentionPolicy> findByTenantIdAndChannel(String tenantId, String channel);
    List<RetentionPolicy> findByTenantId(String tenantId);
}

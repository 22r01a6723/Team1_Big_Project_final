package com.complyvault.retention.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.complyvault.retention.model.RetentionPolicy;

@Repository
public interface RetentionPolicyRepository extends MongoRepository<RetentionPolicy, String> {
    Optional<RetentionPolicy> findByTenantIdAndChannel(String tenantId, String channel);
    List<RetentionPolicy> findByTenantId(String tenantId);
}

package com.complyvault.retention.repository;

import java.time.Instant;
import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.complyvault.retention.model.CanonicalMessage;

@Repository
public interface CanonicalMessageRepository extends MongoRepository<CanonicalMessage, String> {

    // If you want to use the network field instead of context.channel
    List<CanonicalMessage> findByTenantIdAndNetwork(String tenantId, String network);

    // Find messages by tenantId
    List<CanonicalMessage> findByTenantId(String tenantId);

    // Custom delete method if needed
    void deleteById(String id);

    // Find messages before a certain timestamp
    List<CanonicalMessage> findByTimestampBefore(Instant timestamp);
}

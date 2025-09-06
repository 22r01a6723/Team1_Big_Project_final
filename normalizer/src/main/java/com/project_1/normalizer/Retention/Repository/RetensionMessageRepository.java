package com.project_1.normalizer.Retention.Repository;

import com.project_1.normalizer.model.CanonicalMessage;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.time.Instant;
import java.util.List;

@Repository
public interface RetensionMessageRepository extends MongoRepository<CanonicalMessage, String> {

    // If you want to use the network field instead of context.channel
    List<CanonicalMessage> findByTenantIdAndNetwork(String tenantId, String network);

    // Find messages by tenantId
    List<CanonicalMessage> findByTenantId(String tenantId);

    // Custom delete method if needed
    void deleteById(String id);

    // Find messages before a certain timestamp
    List<CanonicalMessage> findByTimestampBefore(Instant timestamp);
}






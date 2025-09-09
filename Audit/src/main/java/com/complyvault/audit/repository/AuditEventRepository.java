package com.complyvault.audit.repository;

import java.time.Instant;
import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import com.complyvault.audit.model.AuditEvent;

@Repository
public interface AuditEventRepository extends MongoRepository<AuditEvent, String> {
    
    // Find audit events by tenant
    List<AuditEvent> findByTenantId(String tenantId);
    
    // Find audit events by message ID
    List<AuditEvent> findByMessageId(String messageId);
    
    // Find audit events by event type
    List<AuditEvent> findByEventType(String eventType);
    
    // Find audit events by service name
    List<AuditEvent> findByServiceName(String serviceName);
    
    // Find audit events within a time range
    List<AuditEvent> findByTimestampBetween(Instant startTime, Instant endTime);
    
    // Find audit events by tenant and time range
    List<AuditEvent> findByTenantIdAndTimestampBetween(String tenantId, Instant startTime, Instant endTime);
    
    // Check if a message ID exists (for duplicate detection)
    boolean existsByMessageId(String messageId);
    
    // Custom query for complex searches
    @Query("{'tenantId': ?0, 'eventType': ?1, 'timestamp': {$gte: ?2, $lte: ?3}}")
    List<AuditEvent> findAuditEventsByTenantAndTypeAndTimeRange(
            String tenantId, String eventType, Instant startTime, Instant endTime);
}

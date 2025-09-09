package com.complyvault.audit.service;

import com.complyvault.audit.model.AuditEvent;
import com.complyvault.audit.repository.AuditEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuditService {

    private final AuditEventRepository auditEventRepository;

    public void logEvent(
            String tenantId,
            String messageId,
            String network,
            String eventType,
            String performedBy,
            String serviceName,
            Map<String, Object> details
    ) {
        AuditEvent event = AuditEvent.builder()
                .id(UUID.randomUUID().toString())
                .tenantId(tenantId)
                .messageId(messageId)
                .network(network)
                .eventType(eventType)
                .timestamp(Instant.now())
                .performedBy(performedBy)
                .serviceName(serviceName)
                .details(details)
                .build();

        auditEventRepository.save(event);
        log.info("Audit event logged: {} for tenant {} message {}", eventType, tenantId, messageId);
    }

    public void logEvent(
            String tenantId,
            String messageId,
            String network,
            String eventType,
            String serviceName,
            Map<String, Object> details
    ) {
        logEvent(tenantId, messageId, network, eventType, serviceName, serviceName, details);
    }

    public List<AuditEvent> getAuditEventsByTenant(String tenantId) {
        return auditEventRepository.findByTenantId(tenantId);
    }

    public List<AuditEvent> getAuditEventsByMessageId(String messageId) {
        return auditEventRepository.findByMessageId(messageId);
    }

    public List<AuditEvent> getAuditEventsByEventType(String eventType) {
        return auditEventRepository.findByEventType(eventType);
    }

    public List<AuditEvent> getAuditEventsByService(String serviceName) {
        return auditEventRepository.findByServiceName(serviceName);
    }

    public List<AuditEvent> getAuditEventsByTimeRange(Instant startTime, Instant endTime) {
        return auditEventRepository.findByTimestampBetween(startTime, endTime);
    }

    public List<AuditEvent> getAuditEventsByTenantAndTimeRange(String tenantId, Instant startTime, Instant endTime) {
        return auditEventRepository.findByTenantIdAndTimestampBetween(tenantId, startTime, endTime);
    }

    public boolean isDuplicate(String messageId) {
        return auditEventRepository.existsByMessageId(messageId);
    }

    public List<AuditEvent> searchAuditEvents(String tenantId, String eventType, Instant startTime, Instant endTime) {
        return auditEventRepository.findAuditEventsByTenantAndTypeAndTimeRange(tenantId, eventType, startTime, endTime);
    }
}

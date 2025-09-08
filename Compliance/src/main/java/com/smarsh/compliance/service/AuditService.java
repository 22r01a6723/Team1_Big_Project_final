package com.smarsh.compliance.service;
import com.smarsh.compliance.exception.ComplianceMongoException;
import com.smarsh.compliance.models.AuditEvent;
import com.smarsh.compliance.mongodb.AuditRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuditService {

    private final AuditRepository auditRepository;

    public void logEvent(
            String tenantId,
            String messageId,
            String network,
            String eventType,
            Map<String, Object> details
    ) {
        AuditEvent evt = AuditEvent.builder()
                .id(UUID.randomUUID().toString())
                .tenantId(tenantId)
                .messageId(messageId)
                .network(network)
                .eventType(eventType)
                .timestamp(Instant.now())
                .performedBy("Compliance-App")
                .details(details)
                .build();
        try {
            auditRepository.save(evt);
        } catch (Exception e) {
            throw new ComplianceMongoException("Failed to save audit event: " + e.getMessage(), e);
        }
    }
}
//indsia tvftsneu

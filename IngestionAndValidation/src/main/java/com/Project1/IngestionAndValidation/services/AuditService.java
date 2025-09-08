package com.Project1.IngestionAndValidation.services;

import com.Project1.IngestionAndValidation.Models.AuditEvent;
import com.Project1.IngestionAndValidation.repository.AuditEventRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

/**
 * SOLID Principles:
 * - SRP: Only responsible for logging audit events.
 * - OCP: Can be extended for new audit event handling strategies.
 * - LSP: Can be used wherever AuditEventListener is expected.
 * - DIP: Depends on AuditEventRepository abstraction.
 */
@Service
public class AuditService implements AuditEventListener {

    private final AuditEventRepository repo;

    public AuditService(AuditEventRepository repo) {
        this.repo = repo;
    }

    public void logEvent(String tenantId,
                         String messageId,
                         String network,
                         String eventType,
                         Map<String, Object> details) {
        AuditEvent event = AuditEvent.builder()
                .auditId(UUID.randomUUID().toString())
                .messageId(messageId)
                .tenantId(tenantId)
                .network(network)
                .eventType(eventType)
                .timestamp(Instant.now())
                .performedBy("IngestionAndValidationApp")
                .details(details)
                .build();

        repo.save(event);

    }

//    public void logDuplicate(String tenantId, String messageId, String network) {
//        AuditEvent event = new AuditEvent();
//        event.setTenantId(tenantId);
//        event.setMessageId(messageId);
//        event.setNetwork(network);
//        event.setAction("DUPLICATE");
//        event.setDetails(Map.of("status", "duplicate detected"));
//        event.setTimestamp(Instant.now());
//
//        repository.save(event);
//    }


    public boolean isDuplicate(String messageId) {
        return repo.existsByMessageId(messageId);
    }

    @Override
    public void onAuditEvent(AuditEvent event) {
        repo.save(event);
    }
}

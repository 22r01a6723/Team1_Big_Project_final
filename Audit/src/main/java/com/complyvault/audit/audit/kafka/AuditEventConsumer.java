package com.complyvault.audit.audit.kafka;

import com.complyvault.audit.audit.model.AuditEvent;
import com.complyvault.audit.audit.service.AuditService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class AuditEventConsumer {

    private final AuditService auditService;

    @KafkaListener(topics = "audit-events", groupId = "audit-service")
    public void consumeAuditEvent(AuditEvent auditEvent) {
        try {
            log.info("Received audit event: {} for tenant {} message {}", 
                    auditEvent.getEventType(), auditEvent.getTenantId(), auditEvent.getMessageId());
            
            // Save the audit event
            auditService.logEvent(
                    auditEvent.getTenantId(),
                    auditEvent.getMessageId(),
                    auditEvent.getNetwork(),
                    auditEvent.getEventType(),
                    auditEvent.getPerformedBy(),
                    auditEvent.getServiceName(),
                    auditEvent.getDetails()
            );
            
            log.info("Successfully processed audit event: {}", auditEvent.getId());
        } catch (Exception e) {
            log.error("Error processing audit event: {}", e.getMessage(), e);
        }
    }

    @KafkaListener(topics = "audit-events-raw", groupId = "audit-service")
    public void consumeRawAuditEvent(Map<String, Object> rawEvent) {
        try {
            log.info("Received raw audit event: {}", rawEvent);
            
            // Extract fields from raw event
            String tenantId = (String) rawEvent.get("tenantId");
            String messageId = (String) rawEvent.get("messageId");
            String network = (String) rawEvent.get("network");
            String eventType = (String) rawEvent.get("eventType");
            String performedBy = (String) rawEvent.getOrDefault("performedBy", "unknown");
            String serviceName = (String) rawEvent.getOrDefault("serviceName", "unknown");
            
            // Remove the fields we've extracted from details
            Map<String, Object> details = rawEvent;
            details.remove("tenantId");
            details.remove("messageId");
            details.remove("network");
            details.remove("eventType");
            details.remove("performedBy");
            details.remove("serviceName");
            
            auditService.logEvent(tenantId, messageId, network, eventType, performedBy, serviceName, details);
            
            log.info("Successfully processed raw audit event for tenant: {}", tenantId);
        } catch (Exception e) {
            log.error("Error processing raw audit event: {}", e.getMessage(), e);
        }
    }
}

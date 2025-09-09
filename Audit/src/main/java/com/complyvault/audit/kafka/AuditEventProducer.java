package com.complyvault.audit.audit.kafka;

import com.complyvault.shared.dto.AuditEventDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class AuditEventProducer {

    private final KafkaTemplate<String, AuditEventDTO> kafkaTemplate;

    public void sendAuditEvent(AuditEventDTO auditEvent) {
        try {
            kafkaTemplate.send("audit-events", auditEvent);
            log.info("Sent audit event to Kafka: {} for tenant {}", 
                    auditEvent.getEventType(), auditEvent.getTenantId());
        } catch (Exception e) {
            log.error("Failed to send audit event to Kafka: {}", e.getMessage(), e);
        }
    }
}

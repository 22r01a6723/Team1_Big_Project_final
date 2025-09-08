package com.complyvault.shared.client;

import com.complyvault.shared.dto.AuditEventDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class AuditClient {

    private final KafkaTemplate<String, AuditEventDTO> kafkaTemplate;

    public void logEvent(
            String tenantId,
            String messageId,
            String network,
            String eventType,
            String serviceName,
            Map<String, Object> details
    ) {
        try {
            AuditEventDTO auditEvent = AuditEventDTO.builder()
                    .tenantId(tenantId)
                    .messageId(messageId)
                    .network(network)
                    .eventType(eventType)
                    .performedBy(serviceName)
                    .serviceName(serviceName)
                    .timestamp(Instant.now())
                    .details(details)
                    .build();

            kafkaTemplate.send("audit-events", auditEvent);
            log.debug("Sent audit event: {} for tenant {} message {}", eventType, tenantId, messageId);
        } catch (Exception e) {
            log.error("Failed to send audit event: {}", e.getMessage(), e);
        }
    }

    public void logEvent(
            String tenantId,
            String messageId,
            String network,
            String eventType,
            String serviceName
    ) {
        logEvent(tenantId, messageId, network, eventType, serviceName, Map.of());
    }
}

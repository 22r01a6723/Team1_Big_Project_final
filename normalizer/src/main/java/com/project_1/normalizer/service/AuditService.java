package com.project_1.normalizer.service;

import com.project_1.normalizer.model.AuditEvent;
import com.project_1.normalizer.repository.AuditRepository;
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
                .performedBy("NormalizerApp")
                .details(details)
                .build();

        auditRepository.save(evt);
    }
}


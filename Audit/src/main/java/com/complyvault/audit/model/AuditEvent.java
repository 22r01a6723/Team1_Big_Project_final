package com.complyvault.audit.model;

import java.time.Instant;
import java.util.Map;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "audit_events")
public class AuditEvent {

    @Id
    private String id;                 // UUID

    private String tenantId;           // e.g., "bank-001"
    private String messageId;          // stableMessageId (optional but useful)
    private String network;             // "email" | "slack"
    private String eventType;          // INGESTED | VALIDATED | CANONICALIZED | COMPLIANCE_CHECKED | RETENTION_PROCESSED | REVIEWED
    private Instant timestamp;         // ISO-like (matches your screenshot)
    private String performedBy;        // Service name that performed the action
    private String serviceName;         // Name of the microservice

    // Free-form details payload (e.g., rawPayload, filePath, collection, etc.)
    private Map<String, Object> details;
}

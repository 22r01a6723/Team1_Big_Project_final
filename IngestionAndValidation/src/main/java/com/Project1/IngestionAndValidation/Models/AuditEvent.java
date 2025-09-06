package com.Project1.IngestionAndValidation.Models;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "audits")
public class AuditEvent {
    @Id
    private String auditId;          // UUID
    private String messageId;        // stable message id (may be null for INGESTED)
    private String tenantId;         // tenant owner
    private String network;          // email/slack
    private String eventType;        // INGESTED | VALIDATED | ID_GENERATED
    private Instant timestamp;       // event time (server)
    private String performedBy;      // "IngestionAndValidationApp"
    private Map<String, Object> details; // arbitrary metadata
}


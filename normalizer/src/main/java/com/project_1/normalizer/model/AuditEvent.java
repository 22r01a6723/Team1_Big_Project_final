package com.project_1.normalizer.model;


import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "audits")
public class AuditEvent {

    @Id
    private String id;                 // UUID

    private String tenantId;           // e.g., "bank-001"
    private String messageId;          // stableMessageId (optional but useful)
    private String network;            // "email" | "slack"
    private String eventType;          // CANONICALIZED | STORED_MONGODB | WRITTEN_DISK
    private Instant timestamp;  // ISO-like (matches your screenshot)
    private String performedBy;        // "NormalizerApp"

    // Free-form details payload (e.g., rawPayload, filePath, collection, etc.)
    private Map<String, Object> details;
}


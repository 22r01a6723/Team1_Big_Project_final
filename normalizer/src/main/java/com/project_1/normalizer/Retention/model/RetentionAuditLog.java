package com.project_1.normalizer.Retention.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "retention_audit_logs")
public class RetentionAuditLog {

    @Id
    private String id;

    private String tenantId;
    private String channel; // email, slack
    private String messageId; // id of the deleted message
    private Instant processedAt; // timestamp when retention was processed
    private Instant cutoffDate; // computed cutoff date for retention
    private Integer retentionDays; // retention policy days
    private String status; // DELETED / NOT_FOUND
    private String details; // additional info or message
}

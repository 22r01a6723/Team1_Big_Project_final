package com.complyvault.retention.model;

import java.time.Instant;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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

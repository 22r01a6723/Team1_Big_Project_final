package com.complyvault.retention.model;


import com.complyvault.retention.model.RetentionAuditLog;
import org.junit.jupiter.api.Test;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class RetentionAuditLogTest {

    @Test
    void retentionAuditLogBuilder_ShouldCreateValidObject() {
        // Act
        RetentionAuditLog auditLog = RetentionAuditLog.builder()
                .id("audit-123")
                .tenantId("tenant-1")
                .channel("email")
                .messageId("msg-456")
                .processedAt(Instant.now())
                .cutoffDate(Instant.now().minusSeconds(86400))
                .retentionDays(30)
                .status("DELETED")
                .details("Message deleted successfully")
                .build();

        // Assert
        assertNotNull(auditLog);
        assertEquals("audit-123", auditLog.getId());
        assertEquals("tenant-1", auditLog.getTenantId());
        assertEquals("email", auditLog.getChannel());
        assertEquals("msg-456", auditLog.getMessageId());
        assertEquals(30, auditLog.getRetentionDays());
        assertEquals("DELETED", auditLog.getStatus());
        assertEquals("Message deleted successfully", auditLog.getDetails());
    }

    @Test
    void documentAnnotation_ShouldBePresent() {
        // Arrange
        Document document = RetentionAuditLog.class.getAnnotation(org.springframework.data.mongodb.core.mapping.Document.class);

        // Assert
        assertNotNull(document);
        assertEquals("retention_audit_logs", document.collection());
    }

    @Test
    void idAnnotation_ShouldBeOnIdField() throws NoSuchFieldException {
        // Arrange
        java.lang.reflect.Field field = RetentionAuditLog.class.getDeclaredField("id");
        org.springframework.data.annotation.Id idAnnotation = field.getAnnotation(org.springframework.data.annotation.Id.class);

        // Assert
        assertNotNull(idAnnotation);
    }

    @Test
    void allArgsConstructor_ShouldWorkCorrectly() {
        // Arrange
        Instant now = Instant.now();
        Instant cutoff = now.minusSeconds(86400);

        // Act
        RetentionAuditLog auditLog = new RetentionAuditLog(
                "audit-1", "tenant-1", "slack", "msg-1",
                now, cutoff, 90, "NOT_FOUND", "No messages found"
        );

        // Assert
        assertEquals("audit-1", auditLog.getId());
        assertEquals("tenant-1", auditLog.getTenantId());
        assertEquals("slack", auditLog.getChannel());
        assertEquals("msg-1", auditLog.getMessageId());
        assertEquals(now, auditLog.getProcessedAt());
        assertEquals(cutoff, auditLog.getCutoffDate());
        assertEquals(90, auditLog.getRetentionDays());
        assertEquals("NOT_FOUND", auditLog.getStatus());
        assertEquals("No messages found", auditLog.getDetails());
    }

    @Test
    void noArgsConstructor_ShouldCreateEmptyObject() {
        // Act
        RetentionAuditLog auditLog = new RetentionAuditLog();

        // Assert
        assertNotNull(auditLog);
        assertNull(auditLog.getId());
        assertNull(auditLog.getTenantId());
        assertNull(auditLog.getChannel());
        assertNull(auditLog.getMessageId());
        assertNull(auditLog.getProcessedAt());
        assertNull(auditLog.getCutoffDate());
        assertNull(auditLog.getRetentionDays());
        assertNull(auditLog.getStatus());
        assertNull(auditLog.getDetails());
    }

    @Test
    void settersAndGetters_ShouldWorkCorrectly() {
        // Arrange
        RetentionAuditLog auditLog = new RetentionAuditLog();
        Instant now = Instant.now();

        // Act
        auditLog.setId("test-id");
        auditLog.setTenantId("test-tenant");
        auditLog.setChannel("test-channel");
        auditLog.setMessageId("test-message");
        auditLog.setProcessedAt(now);
        auditLog.setCutoffDate(now.minusSeconds(3600));
        auditLog.setRetentionDays(60);
        auditLog.setStatus("TEST_STATUS");
        auditLog.setDetails("Test details");

        // Assert
        assertEquals("test-id", auditLog.getId());
        assertEquals("test-tenant", auditLog.getTenantId());
        assertEquals("test-channel", auditLog.getChannel());
        assertEquals("test-message", auditLog.getMessageId());
        assertEquals(now, auditLog.getProcessedAt());
        assertEquals(60, auditLog.getRetentionDays());
        assertEquals("TEST_STATUS", auditLog.getStatus());
        assertEquals("Test details", auditLog.getDetails());
    }


    @Test
    void builder_ShouldHandleNullOptionalFields() {
        // Act
        RetentionAuditLog log = RetentionAuditLog.builder()
                .id("audit-null")
                .build();

        // Assert
        assertNotNull(log);
        assertEquals("audit-null", log.getId());
        assertNull(log.getTenantId());
        assertNull(log.getChannel());
        assertNull(log.getProcessedAt());
    }

    @Test
    void retentionDays_ShouldAllowZeroAndNegativeValues() {
        // Arrange & Act
        RetentionAuditLog zeroDays = RetentionAuditLog.builder()
                .id("zero")
                .retentionDays(0)
                .build();

        RetentionAuditLog negativeDays = RetentionAuditLog.builder()
                .id("negative")
                .retentionDays(-10)
                .build();

        // Assert
        assertEquals(0, zeroDays.getRetentionDays());
        assertEquals(-10, negativeDays.getRetentionDays());
    }


}
package com.complyvault.audit.event;

import com.complyvault.audit.audit.model.AuditEvent;
import org.junit.jupiter.api.Test;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class AuditEventTest {

    @Test
    void auditEventBuilder_ShouldCreateValidObject() {
        // Act
        AuditEvent auditEvent = AuditEvent.builder()
                .id("audit-1")
                .tenantId("bank-001")
                .messageId("msg-123")
                .network("email")
                .eventType("INGESTED")
                .timestamp(Instant.now())
                .performedBy("ingestion-service")
                .serviceName("ingestion-service")
                .details(Map.of("filePath", "/path/to/file"))
                .build();

        // Assert
        assertNotNull(auditEvent);
        assertEquals("audit-1", auditEvent.getId());
        assertEquals("bank-001", auditEvent.getTenantId());
        assertEquals("msg-123", auditEvent.getMessageId());
        assertEquals("email", auditEvent.getNetwork());
        assertEquals("INGESTED", auditEvent.getEventType());
        assertNotNull(auditEvent.getTimestamp());
        assertEquals("ingestion-service", auditEvent.getPerformedBy());
        assertEquals("ingestion-service", auditEvent.getServiceName());
        assertEquals("/path/to/file", auditEvent.getDetails().get("filePath"));
    }

    @Test
    void documentAnnotation_ShouldBePresent() {
        // Arrange
        Document document = AuditEvent.class.getAnnotation(org.springframework.data.mongodb.core.mapping.Document.class);

        // Assert
        assertNotNull(document);
        assertEquals("audit_events", document.collection());
    }

    @Test
    void idAnnotation_ShouldBeOnIdField() throws NoSuchFieldException {
        // Arrange
        java.lang.reflect.Field field = AuditEvent.class.getDeclaredField("id");
        org.springframework.data.annotation.Id idAnnotation = field.getAnnotation(org.springframework.data.annotation.Id.class);

        // Assert
        assertNotNull(idAnnotation);
    }

    @Test
    void allArgsConstructor_ShouldWorkCorrectly() {
        // Arrange
        Instant now = Instant.now();
        Map<String, Object> details = Map.of("key", "value");

        // Act
        AuditEvent auditEvent = new AuditEvent(
                "audit-1", "bank-001", "msg-123", "email", "INGESTED",
                now, "ingestion-service", "ingestion-service", details
        );

        // Assert
        assertEquals("audit-1", auditEvent.getId());
        assertEquals("bank-001", auditEvent.getTenantId());
        assertEquals("msg-123", auditEvent.getMessageId());
        assertEquals("email", auditEvent.getNetwork());
        assertEquals("INGESTED", auditEvent.getEventType());
        assertEquals(now, auditEvent.getTimestamp());
        assertEquals("ingestion-service", auditEvent.getPerformedBy());
        assertEquals("ingestion-service", auditEvent.getServiceName());
        assertEquals("value", auditEvent.getDetails().get("key"));
    }

    @Test
    void noArgsConstructor_ShouldCreateEmptyObject() {
        // Act
        AuditEvent auditEvent = new AuditEvent();

        // Assert
        assertNotNull(auditEvent);
        assertNull(auditEvent.getId());
        assertNull(auditEvent.getTenantId());
        assertNull(auditEvent.getMessageId());
        assertNull(auditEvent.getNetwork());
        assertNull(auditEvent.getEventType());
        assertNull(auditEvent.getTimestamp());
        assertNull(auditEvent.getPerformedBy());
        assertNull(auditEvent.getServiceName());
        assertNull(auditEvent.getDetails());
    }

    // Additional test cases

    @Test
    void settersAndGetters_ShouldWorkCorrectly() {
        // Arrange
        AuditEvent auditEvent = new AuditEvent();
        Instant now = Instant.now();
        Map<String, Object> details = Map.of("testKey", "testValue");

        // Act
        auditEvent.setId("test-id");
        auditEvent.setTenantId("test-tenant");
        auditEvent.setMessageId("test-message");
        auditEvent.setNetwork("test-network");
        auditEvent.setEventType("test-event");
        auditEvent.setTimestamp(now);
        auditEvent.setPerformedBy("test-performer");
        auditEvent.setServiceName("test-service");
        auditEvent.setDetails(details);

        // Assert
        assertEquals("test-id", auditEvent.getId());
        assertEquals("test-tenant", auditEvent.getTenantId());
        assertEquals("test-message", auditEvent.getMessageId());
        assertEquals("test-network", auditEvent.getNetwork());
        assertEquals("test-event", auditEvent.getEventType());
        assertEquals(now, auditEvent.getTimestamp());
        assertEquals("test-performer", auditEvent.getPerformedBy());
        assertEquals("test-service", auditEvent.getServiceName());
        assertEquals("testValue", auditEvent.getDetails().get("testKey"));
    }

    @Test
    void equalsAndHashCode_ShouldWorkForSameObjects() {
        // Arrange
        Instant now = Instant.now();
        Map<String, Object> details = Map.of("key", "value");

        AuditEvent event1 = new AuditEvent(
                "id-1", "tenant-1", "msg-1", "email", "INGESTED",
                now, "service-1", "service-1", details
        );

        AuditEvent event2 = new AuditEvent(
                "id-1", "tenant-1", "msg-1", "email", "INGESTED",
                now, "service-1", "service-1", details
        );

        // Assert
        assertEquals(event1, event2);
        assertEquals(event1.hashCode(), event2.hashCode());
    }

    @Test
    void equalsAndHashCode_ShouldWorkForDifferentObjects() {
        // Arrange
        Instant now = Instant.now();
        Map<String, Object> details = Map.of("key", "value");

        AuditEvent event1 = new AuditEvent(
                "id-1", "tenant-1", "msg-1", "email", "INGESTED",
                now, "service-1", "service-1", details
        );

        AuditEvent event2 = new AuditEvent(
                "id-2", "tenant-2", "msg-2", "slack", "VALIDATED",
                now, "service-2", "service-2", details
        );

        // Assert
        assertNotEquals(event1, event2);
        assertNotEquals(event1.hashCode(), event2.hashCode());
    }

    @Test
    void equals_ShouldReturnFalseForNull() {
        // Arrange
        AuditEvent auditEvent = AuditEvent.builder()
                .id("test-id")
                .tenantId("test-tenant")
                .build();

        // Assert
        assertNotEquals(auditEvent, null);
    }

    @Test
    void equals_ShouldReturnFalseForDifferentClass() {
        // Arrange
        AuditEvent auditEvent = AuditEvent.builder()
                .id("test-id")
                .tenantId("test-tenant")
                .build();

        // Assert
        assertNotEquals(auditEvent, "string-object");
    }

    @Test
    void toString_ShouldContainAllFields() {
        // Arrange
        AuditEvent auditEvent = AuditEvent.builder()
                .id("test-id")
                .tenantId("test-tenant")
                .messageId("test-message")
                .network("test-network")
                .eventType("test-event")
                .timestamp(Instant.parse("2023-01-01T00:00:00Z"))
                .performedBy("test-performer")
                .serviceName("test-service")
                .details(Map.of("key", "value"))
                .build();

        // Act
        String toStringResult = auditEvent.toString();

        // Assert
        assertTrue(toStringResult.contains("test-id"));
        assertTrue(toStringResult.contains("test-tenant"));
        assertTrue(toStringResult.contains("test-message"));
        assertTrue(toStringResult.contains("test-network"));
        assertTrue(toStringResult.contains("test-event"));
        assertTrue(toStringResult.contains("2023-01-01T00:00:00Z"));
        assertTrue(toStringResult.contains("test-performer"));
        assertTrue(toStringResult.contains("test-service"));
        assertTrue(toStringResult.contains("key=value"));
    }

    @Test
    void builder_WithNullValues_ShouldHandleGracefully() {
        // Act
        AuditEvent auditEvent = AuditEvent.builder()
                .id(null)
                .tenantId(null)
                .messageId(null)
                .network(null)
                .eventType(null)
                .timestamp(null)
                .performedBy(null)
                .serviceName(null)
                .details(null)
                .build();

        // Assert
        assertNotNull(auditEvent);
        assertNull(auditEvent.getId());
        assertNull(auditEvent.getTenantId());
        assertNull(auditEvent.getMessageId());
        assertNull(auditEvent.getNetwork());
        assertNull(auditEvent.getEventType());
        assertNull(auditEvent.getTimestamp());
        assertNull(auditEvent.getPerformedBy());
        assertNull(auditEvent.getServiceName());
        assertNull(auditEvent.getDetails());
    }

    @Test
    void builder_WithEmptyStrings_ShouldWorkCorrectly() {
        // Act
        AuditEvent auditEvent = AuditEvent.builder()
                .id("")
                .tenantId("")
                .messageId("")
                .network("")
                .eventType("")
                .performedBy("")
                .serviceName("")
                .details(Map.of())
                .build();

        // Assert
        assertEquals("", auditEvent.getId());
        assertEquals("", auditEvent.getTenantId());
        assertEquals("", auditEvent.getMessageId());
        assertEquals("", auditEvent.getNetwork());
        assertEquals("", auditEvent.getEventType());
        assertEquals("", auditEvent.getPerformedBy());
        assertEquals("", auditEvent.getServiceName());
        assertTrue(auditEvent.getDetails().isEmpty());
    }

    @Test
    void builder_WithComplexDetails_ShouldWorkCorrectly() {
        // Arrange
        Map<String, Object> complexDetails = new HashMap<>();
        complexDetails.put("string", "value");
        complexDetails.put("number", 123);
        complexDetails.put("boolean", true);
        complexDetails.put("nested", Map.of("inner", "value"));

        // Act
        AuditEvent auditEvent = AuditEvent.builder()
                .id("complex-id")
                .tenantId("complex-tenant")
                .details(complexDetails)
                .build();

        // Assert
        assertEquals("complex-id", auditEvent.getId());
        assertEquals("complex-tenant", auditEvent.getTenantId());
        assertEquals("value", auditEvent.getDetails().get("string"));
        assertEquals(123, auditEvent.getDetails().get("number"));
        assertEquals(true, auditEvent.getDetails().get("boolean"));
        assertEquals("value", ((Map<?, ?>) auditEvent.getDetails().get("nested")).get("inner"));
    }

}
package com.Project1.IngestionAndValidation.event;


import com.Project1.IngestionAndValidation.Models.AuditEvent;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class AuditEventTest {

    @Test
    void testAuditEventBuilder() {
        Instant timestamp = Instant.now();
        AuditEvent event = AuditEvent.builder()
                .auditId("audit-123")
                .messageId("msg-456")
                .tenantId("tenant-1")
                .network("email")
                .eventType("VALIDATED")
                .timestamp(timestamp)
                .performedBy("TestApp")
                .details(Map.of("key", "value"))
                .build();

        assertEquals("audit-123", event.getAuditId());
        assertEquals("msg-456", event.getMessageId());
        assertEquals("tenant-1", event.getTenantId());
        assertEquals("email", event.getNetwork());
        assertEquals("VALIDATED", event.getEventType());
        assertEquals(timestamp, event.getTimestamp());
        assertEquals("TestApp", event.getPerformedBy());
        assertEquals("value", event.getDetails().get("key"));
    }

    @Test
    void testAuditEventNoArgsConstructor() {
        AuditEvent event = new AuditEvent();

        assertNull(event.getAuditId());
        assertNull(event.getMessageId());
        assertNull(event.getTenantId());
        assertNull(event.getNetwork());
        assertNull(event.getEventType());
        assertNull(event.getTimestamp());
        assertNull(event.getPerformedBy());
        assertNull(event.getDetails());
    }

    @Test
    void testAuditEventAllArgsConstructor() {
        Instant timestamp = Instant.now();
        Map<String, Object> details = Map.of("test", "value");

        AuditEvent event = new AuditEvent(
                "audit-1", "msg-1", "tenant-1", "email",
                "TEST_EVENT", timestamp, "TestApp", details
        );

        assertEquals("audit-1", event.getAuditId());
        assertEquals("msg-1", event.getMessageId());
        assertEquals("tenant-1", event.getTenantId());
        assertEquals("email", event.getNetwork());
        assertEquals("TEST_EVENT", event.getEventType());
        assertEquals(timestamp, event.getTimestamp());
        assertEquals("TestApp", event.getPerformedBy());
        assertEquals("value", event.getDetails().get("test"));
    }

    @Test
    void testSettersAndGetters() {
        AuditEvent event = new AuditEvent();
        Instant now = Instant.now();
        Map<String, Object> details = new HashMap<>();
        details.put("foo", "bar");

        event.setAuditId("id-1");
        event.setMessageId("msg-1");
        event.setTenantId("tenant-1");
        event.setNetwork("slack");
        event.setEventType("INGESTED");
        event.setTimestamp(now);
        event.setPerformedBy("System");
        event.setDetails(details);

        assertEquals("id-1", event.getAuditId());
        assertEquals("msg-1", event.getMessageId());
        assertEquals("tenant-1", event.getTenantId());
        assertEquals("slack", event.getNetwork());
        assertEquals("INGESTED", event.getEventType());
        assertEquals(now, event.getTimestamp());
        assertEquals("System", event.getPerformedBy());
        assertEquals("bar", event.getDetails().get("foo"));
    }

    @Test
    void testEqualsAndHashCode_SameValues() {
        Instant ts = Instant.now();
        Map<String, Object> details = Map.of("x", 1);

        AuditEvent e1 = new AuditEvent("a1", "m1", "t1", "email", "EVENT", ts, "App", details);
        AuditEvent e2 = new AuditEvent("a1", "m1", "t1", "email", "EVENT", ts, "App", details);

        assertEquals(e1, e2);
        assertEquals(e1.hashCode(), e2.hashCode());
    }

    @Test
    void testEqualsAndHashCode_DifferentValues() {
        AuditEvent e1 = new AuditEvent("a1", "m1", "t1", "email", "EVENT", Instant.now(), "App", Map.of());
        AuditEvent e2 = new AuditEvent("a2", "m2", "t2", "slack", "DIFF", Instant.now(), "OtherApp", Map.of());

        assertNotEquals(e1, e2);
        assertNotEquals(e1.hashCode(), e2.hashCode());
    }

    @Test
    void testToStringContainsFields() {
        AuditEvent event = AuditEvent.builder()
                .auditId("a123")
                .messageId("m123")
                .tenantId("tenant-x")
                .network("teams")
                .eventType("ARCHIVED")
                .performedBy("Tester")
                .details(Map.of("k", "v"))
                .build();

        String str = event.toString();
        assertTrue(str.contains("a123"));
        assertTrue(str.contains("m123"));
        assertTrue(str.contains("tenant-x"));
        assertTrue(str.contains("ARCHIVED"));
        assertTrue(str.contains("Tester"));
    }

    @Test
    void testBuilderWithNulls() {
        AuditEvent event = AuditEvent.builder()
                .auditId(null)
                .messageId(null)
                .tenantId(null)
                .network(null)
                .eventType(null)
                .timestamp(null)
                .performedBy(null)
                .details(null)
                .build();

        assertNull(event.getAuditId());
        assertNull(event.getMessageId());
        assertNull(event.getTenantId());
        assertNull(event.getNetwork());
        assertNull(event.getEventType());
        assertNull(event.getTimestamp());
        assertNull(event.getPerformedBy());
        assertNull(event.getDetails());
    }

    @Test
    void testEqualsSameReference() {
        AuditEvent event = new AuditEvent();
        assertEquals(event, event);
    }

    @Test
    void testEqualsWithNull() {
        AuditEvent event = new AuditEvent();
        assertNotEquals(event, null);
    }

    @Test
    void testEqualsWithDifferentClass() {
        AuditEvent event = new AuditEvent();
        assertNotEquals(event, "string");
    }

    @Test
    void testMutabilityOfDetailsMap() {
        Map<String, Object> details = new HashMap<>();
        AuditEvent event = new AuditEvent("a", "m", "t", "net", "ET", Instant.now(), "App", details);

        details.put("newKey", "newValue");
        assertEquals("newValue", event.getDetails().get("newKey"));
    }
}
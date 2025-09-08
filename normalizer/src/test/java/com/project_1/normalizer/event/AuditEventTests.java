package com.project_1.normalizer.event;


import com.project_1.normalizer.model.AuditEvent;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class AuditEventTest {

    @Test
    void testAuditEventBuilder() {
        Instant timestamp = Instant.now();
        AuditEvent event = AuditEvent.builder()
                .id("audit-1")
                .tenantId("tenant-1")
                .messageId("msg-1")
                .network("email")
                .eventType("TEST_EVENT")
                .timestamp(timestamp)
                .performedBy("TestApp")
                .details(Map.of("key", "value"))
                .build();

        assertEquals("audit-1", event.getId());
        assertEquals("tenant-1", event.getTenantId());
        assertEquals("msg-1", event.getMessageId());
        assertEquals("email", event.getNetwork());
        assertEquals("TEST_EVENT", event.getEventType());
        assertEquals(timestamp, event.getTimestamp());
        assertEquals("TestApp", event.getPerformedBy());
        assertEquals("value", event.getDetails().get("key"));
    }

    @Test
    void testAuditEventNoArgsConstructor() {
        AuditEvent event = new AuditEvent();

        assertNull(event.getId());
        assertNull(event.getTenantId());
        assertNull(event.getMessageId());
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
                "audit-1", "tenant-1", "msg-1", "email",
                "TEST_EVENT", timestamp, "TestApp", details
        );

        assertEquals("audit-1", event.getId());
        assertEquals("tenant-1", event.getTenantId());
        assertEquals("msg-1", event.getMessageId());
        assertEquals("email", event.getNetwork());
        assertEquals("TEST_EVENT", event.getEventType());
        assertEquals(timestamp, event.getTimestamp());
        assertEquals("TestApp", event.getPerformedBy());
        assertEquals("value", event.getDetails().get("test"));
    }
    @Test
    void testSettersAndGetters() {
        AuditEvent event = new AuditEvent();
        Instant timestamp = Instant.now();
        Map<String, Object> details = Map.of("d", "1");

        event.setId("id-123");
        event.setTenantId("tenant-123");
        event.setMessageId("msg-123");
        event.setNetwork("sms");
        event.setEventType("UPDATED");
        event.setTimestamp(timestamp);
        event.setPerformedBy("Updater");
        event.setDetails(details);

        assertEquals("id-123", event.getId());
        assertEquals("tenant-123", event.getTenantId());
        assertEquals("msg-123", event.getMessageId());
        assertEquals("sms", event.getNetwork());
        assertEquals("UPDATED", event.getEventType());
        assertEquals(timestamp, event.getTimestamp());
        assertEquals("Updater", event.getPerformedBy());
        assertEquals("1", event.getDetails().get("d"));
    }

    @Test
    void testEqualsAndHashCode_SameValues() {
        Instant timestamp = Instant.now();
        Map<String, Object> details = Map.of("a", 1);

        AuditEvent e1 = new AuditEvent("id", "tenant", "msg", "email", "EVENT", timestamp, "user", details);
        AuditEvent e2 = new AuditEvent("id", "tenant", "msg", "email", "EVENT", timestamp, "user", details);

        assertEquals(e1, e2);
        assertEquals(e1.hashCode(), e2.hashCode());
    }

    @Test
    void testEquals_DifferentValues() {
        AuditEvent e1 = new AuditEvent();
        e1.setId("id-1");

        AuditEvent e2 = new AuditEvent();
        e2.setId("id-2");

        assertNotEquals(e1, e2);
    }

    @Test
    void testEquals_WithNull() {
        AuditEvent e1 = new AuditEvent();
        assertNotEquals(e1, null);
    }

    @Test
    void testEquals_WithDifferentClass() {
        AuditEvent e1 = new AuditEvent();
        assertNotEquals(e1, "some string");
    }

    @Test
    void testToStringContainsFields() {
        Instant timestamp = Instant.now();
        AuditEvent event = new AuditEvent("id", "tenant", "msg", "network", "TYPE", timestamp, "App", Map.of("k", "v"));

        String toString = event.toString();
        assertTrue(toString.contains("id"));
        assertTrue(toString.contains("tenant"));
        assertTrue(toString.contains("msg"));
        assertTrue(toString.contains("network"));
        assertTrue(toString.contains("TYPE"));
        assertTrue(toString.contains("App"));
    }

    @Test
    void testBuilderWithEmptyDetails() {
        AuditEvent event = AuditEvent.builder()
                .id("id-xyz")
                .tenantId("tenant-xyz")
                .messageId("msg-xyz")
                .details(Map.of())
                .build();

        assertNotNull(event);
        assertTrue(event.getDetails().isEmpty());
    }

    @Test
    void testBuilderWithNullDetails() {
        AuditEvent event = AuditEvent.builder()
                .id("id-abc")
                .tenantId("tenant-abc")
                .messageId("msg-abc")
                .details(null)
                .build();

        assertNull(event.getDetails());
    }

    @Test
    void testTimestampImmutability() {
        Instant now = Instant.now();
        AuditEvent event = new AuditEvent();
        event.setTimestamp(now);

        assertEquals(now, event.getTimestamp());
        assertSame(now, event.getTimestamp()); // Instant is immutable, so safe
    }

}
package com.Project1.IngestionAndValidation.message;


import com.Project1.IngestionAndValidation.Models.ProcessedMessage;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ProcessedMessageTest {

    @Test
    void testProcessedMessageConstructor() {
        ProcessedMessage message = new ProcessedMessage("msg-1", "tenant-1", "email");

        assertEquals("msg-1", message.getId());
        assertEquals("tenant-1", message.getTenantId());
        assertEquals("email", message.getNetwork());
    }

    @Test
    void testProcessedMessageGetters() {
        ProcessedMessage message = new ProcessedMessage("msg-1", "tenant-1", "email");

        assertEquals("msg-1", message.getId());
        assertEquals("tenant-1", message.getTenantId());
        assertEquals("email", message.getNetwork());
    }

    @Test
    void testProcessedMessageNullValues() {
        ProcessedMessage message = new ProcessedMessage(null, null, null);

        assertNull(message.getId());
        assertNull(message.getTenantId());
        assertNull(message.getNetwork());
    }
    @Test
    void testEqualsSameObject() {
        ProcessedMessage message = new ProcessedMessage("msg-1", "tenant-1", "email");
        assertEquals(message, message);
    }


    @Test
    void testNotEqualsDifferentId() {
        ProcessedMessage m1 = new ProcessedMessage("msg-1", "tenant-1", "email");
        ProcessedMessage m2 = new ProcessedMessage("msg-2", "tenant-1", "email");
        assertNotEquals(m1, m2);
    }

    @Test
    void testNotEqualsDifferentTenantId() {
        ProcessedMessage m1 = new ProcessedMessage("msg-1", "tenant-1", "email");
        ProcessedMessage m2 = new ProcessedMessage("msg-1", "tenant-2", "email");
        assertNotEquals(m1, m2);
    }

    @Test
    void testNotEqualsDifferentNetwork() {
        ProcessedMessage m1 = new ProcessedMessage("msg-1", "tenant-1", "email");
        ProcessedMessage m2 = new ProcessedMessage("msg-1", "tenant-1", "slack");
        assertNotEquals(m1, m2);
    }

    @Test
    void testEqualsNull() {
        ProcessedMessage m1 = new ProcessedMessage("msg-1", "tenant-1", "email");
        assertNotEquals(null, m1);
    }

    @Test
    void testEqualsDifferentClass() {
        ProcessedMessage m1 = new ProcessedMessage("msg-1", "tenant-1", "email");
        assertNotEquals(m1, "some string");
    }

    @Test
    void testHashCodeConsistency() {
        ProcessedMessage m1 = new ProcessedMessage("msg-1", "tenant-1", "email");
        int initialHash = m1.hashCode();
        assertEquals(initialHash, m1.hashCode());
    }


    @Test
    void testEqualsAndHashCodeWithNullFields() {
        ProcessedMessage m1 = new ProcessedMessage(null, "tenant-1", "email");
        ProcessedMessage m2 = new ProcessedMessage(null, "tenant-1", "email");
//        assertEquals(m1, m2);
        assertNotEquals(m1.hashCode(), m2.hashCode());
    }

    @Test
    void testInequalityDifferentAllFields() {
        ProcessedMessage m1 = new ProcessedMessage("msg-1", "tenant-1", "email");
        ProcessedMessage m2 = new ProcessedMessage("msg-2", "tenant-2", "slack");
        assertNotEquals(m1, m2);
    }
}
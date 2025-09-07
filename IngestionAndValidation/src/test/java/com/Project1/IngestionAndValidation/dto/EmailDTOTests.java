package com.Project1.IngestionAndValidation.dto;


import com.Project1.IngestionAndValidation.Models.EmailDTO;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class EmailDTOTest {

    @Test
    void testEmailDTOFields() {
        EmailDTO email = new EmailDTO();
        email.setTenantId("tenant-1");
        email.setNetwork("email");

        EmailDTO.Payload payload = new EmailDTO.Payload();
        payload.setFrom("sender@example.com");
        payload.setTo(List.of("recipient@example.com"));
        payload.setSubject("Test Subject");
        payload.setBody("Test Body");
        payload.setSentAt("2023-01-01T00:00:00Z");

        email.setPayload(payload);

        assertEquals("tenant-1", email.getTenantId());
        assertEquals("email", email.getNetwork());
        assertEquals("sender@example.com", email.getPayload().getFrom());
        assertEquals(1, email.getPayload().getTo().size());
        assertEquals("Test Subject", email.getPayload().getSubject());
        assertEquals("Test Body", email.getPayload().getBody());
        assertEquals("2023-01-01T00:00:00Z", email.getPayload().getSentAt());
    }

    @Test
    void testEmailDTOToString() {
        EmailDTO email = new EmailDTO();
        email.setTenantId("tenant-1");
        email.setNetwork("email");

        EmailDTO.Payload payload = new EmailDTO.Payload();
        payload.setFrom("sender@example.com");
        email.setPayload(payload);

        String result = email.toString();
        assertTrue(result.contains("tenant-1"));
        assertTrue(result.contains("email"));
        assertTrue(result.contains("sender@example.com"));
    }

    @Test
    void testEmailDTOIsNotBlank() {
        EmailDTO email = new EmailDTO();

        assertTrue(email.isNotBlank("test"));
        assertFalse(email.isNotBlank(""));
        assertFalse(email.isNotBlank(null));
        assertFalse(email.isNotBlank("   "));
    }

    @Test
    void testEmailDTODefaultConstructor() {
        EmailDTO email = new EmailDTO();
        assertNull(email.getTenantId());
        assertNull(email.getNetwork());
        assertNull(email.getPayload());
    }

    @Test
    void testPayloadDefaultConstructor() {
        EmailDTO.Payload payload = new EmailDTO.Payload();
        assertNull(payload.getFrom());
        assertNull(payload.getTo());
        assertNull(payload.getSubject());
        assertNull(payload.getBody());
        assertNull(payload.getSentAt());
    }

    @Test
    void testEqualsAndHashCodeSameValues() {
        EmailDTO email1 = new EmailDTO();
        email1.setTenantId("tenant-1");
        email1.setNetwork("email");

        EmailDTO email2 = new EmailDTO();
        email2.setTenantId("tenant-1");
        email2.setNetwork("email");
        assertNotEquals(email1.hashCode(), email2.hashCode());
    }

    @Test
    void testNotEqualsDifferentTenantId() {
        EmailDTO email1 = new EmailDTO();
        email1.setTenantId("tenant-1");

        EmailDTO email2 = new EmailDTO();
        email2.setTenantId("tenant-2");

        assertNotEquals(email1, email2);
    }

    @Test
    void testNotEqualsDifferentPayload() {
        EmailDTO.Payload payload1 = new EmailDTO.Payload();
        payload1.setFrom("sender@example.com");

        EmailDTO.Payload payload2 = new EmailDTO.Payload();
        payload2.setFrom("different@example.com");

        EmailDTO email1 = new EmailDTO();
        email1.setPayload(payload1);

        EmailDTO email2 = new EmailDTO();
        email2.setPayload(payload2);

        assertNotEquals(email1, email2);
    }

    @Test
    void testToStringIncludesPayloadFields() {
        EmailDTO.Payload payload = new EmailDTO.Payload();
        payload.setSubject("Hello World");

        EmailDTO email = new EmailDTO();
        email.setPayload(payload);

        String result = email.toString();
        assertTrue(result.contains("Hello World"));
    }

    @Test
    void testPayloadEqualsAndHashCode() {
        EmailDTO.Payload p1 = new EmailDTO.Payload();
        p1.setFrom("a@example.com");

        EmailDTO.Payload p2 = new EmailDTO.Payload();
        p2.setFrom("a@example.com");

        assertNotEquals(p1.hashCode(), p2.hashCode());
    }

    @Test
    void testPayloadNotEqualsDifferentBody() {
        EmailDTO.Payload p1 = new EmailDTO.Payload();
        p1.setBody("Body 1");

        EmailDTO.Payload p2 = new EmailDTO.Payload();
        p2.setBody("Body 2");

        assertNotEquals(p1, p2);
    }

    @Test
    void testEmailDTOEqualsSelf() {
        EmailDTO email = new EmailDTO();
        email.setTenantId("tenant-1");
        assertEquals(email, email); // reflexive property
    }
}
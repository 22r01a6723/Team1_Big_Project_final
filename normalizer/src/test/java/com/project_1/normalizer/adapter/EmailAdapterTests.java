package com.project_1.normalizer.adapter;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project_1.normalizer.model.CanonicalMessage;
import com.project_1.normalizer.util.adapters.EmailAdapter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class EmailAdapterTest {

    private EmailAdapter emailAdapter;
    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        emailAdapter = new EmailAdapter();
    }

    @Test
    void testSupports_EmailNetwork_ReturnsTrue() {
        assertTrue(emailAdapter.supports("email"));
        assertTrue(emailAdapter.supports("EMAIL"));
    }

    @Test
    void testSupports_NonEmailNetwork_ReturnsFalse() {
        assertFalse(emailAdapter.supports("slack"));
        assertFalse(emailAdapter.supports("unknown"));
        assertFalse(emailAdapter.supports(null));
    }

    @Test
    void testMap_ValidEmailJson_ReturnsCanonicalMessage() throws Exception {
        String json = """
        {
            "stableMessageId": "msg-1",
            "tenantId": "tenant-1",
            "payload": {
                "from": "sender@example.com",
                "to": ["recipient1@example.com", "recipient2@example.com"],
                "subject": "Test Subject",
                "body": "Test Body",
                "sentAt": "2023-01-01T00:00:00Z"
            }
        }
        """;

        JsonNode root = objectMapper.readTree(json);
        CanonicalMessage message = emailAdapter.map(root);

        assertEquals("msg-1", message.getMessageId());
        assertEquals("tenant-1", message.getTenantId());
        assertEquals("email", message.getNetwork());
        assertEquals(Instant.parse("2023-01-01T00:00:00Z"), message.getTimestamp());

        assertEquals(3, message.getParticipants().size()); // sender + 2 recipients
        assertEquals("Test Subject", message.getContent().getSubject());
        assertEquals("Test Body", message.getContent().getBody());
    }

    @Test
    void testMap_NullRoot_ThrowsException() {
        assertThrows(NullPointerException.class, () -> emailAdapter.map(null));
    }


    @Test
    void testMap_MalformedTimestamp_ThrowsException() throws Exception {
        String json = """
        {
            "stableMessageId": "msg-4",
            "tenantId": "tenant-4",
            "payload": {
                "from": "sender@example.com",
                "to": ["recipient@example.com"],
                "sentAt": "invalid-date"
            }
        }
        """;

        JsonNode root = objectMapper.readTree(json);

        assertThrows(Exception.class, () -> emailAdapter.map(root));
    }



}

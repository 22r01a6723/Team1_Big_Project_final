package com.project_1.normalizer.adapter;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project_1.normalizer.model.CanonicalMessage;
import com.project_1.normalizer.util.adapters.SlackAdapter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class SlackAdapterTest {

    private SlackAdapter slackAdapter;
    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        slackAdapter = new SlackAdapter();
    }

    @Test
    void testSupports_SlackNetwork_ReturnsTrue() {
        assertTrue(slackAdapter.supports("slack"));
        assertTrue(slackAdapter.supports("SLACK"));
    }

    @Test
    void testSupports_NonSlackNetwork_ReturnsFalse() {
        assertFalse(slackAdapter.supports("email"));
        assertFalse(slackAdapter.supports("unknown"));
        assertFalse(slackAdapter.supports(null));
    }

    @Test
    void testMap_ValidSlackJson_ReturnsCanonicalMessage() throws Exception {
        String json = """
        {
            "stableMessageId": "msg-1",
            "tenantId": "tenant-1",
            "user": "user1",
            "text": "Hello world",
            "timestamp": "2023-01-01T00:00:00Z",
            "team": "team1",
            "channel": "general",
            "rawReference": "ref-123"
        }
        """;

        JsonNode root = objectMapper.readTree(json);
        CanonicalMessage message = slackAdapter.map(root);

        assertEquals("msg-1", message.getMessageId());
        assertEquals("tenant-1", message.getTenantId());
        assertEquals("slack", message.getNetwork());
        assertEquals(Instant.parse("2023-01-01T00:00:00Z"), message.getTimestamp());

        assertEquals(1, message.getParticipants().size());
        assertEquals("user1", message.getParticipants().get(0).getId());
        assertEquals("Hello world", message.getContent().getBody());
        assertEquals("team1", message.getContext().getTeam());
        assertEquals("general", message.getContext().getChannel());
    }


    @Test
    void testMap_NullRoot_ThrowsException() {
        assertThrows(NullPointerException.class, () -> slackAdapter.map(null));
    }
}
package com.Project1.IngestionAndValidation.dto;


import com.Project1.IngestionAndValidation.Models.SlackDTO;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SlackDTOTests {

    @Test
    void testSlackDTOFields() {
        SlackDTO slack = new SlackDTO();
        slack.setTenantId("tenant-1");
        slack.setStableMessageId("stable-123");
        slack.setNetwork("slack");
        slack.setMessageId("msg-1");
        slack.setUser("user1");
        slack.setText("Hello world");
        slack.setTimestamp("2023-01-01T00:00:00Z");
        slack.setTeam("team1");
        slack.setChannel("general");
        slack.setRawReference("ref-123");

        assertEquals("tenant-1", slack.getTenantId());
        assertEquals("stable-123", slack.getStableMessageId());
        assertEquals("slack", slack.getNetwork());
        assertEquals("msg-1", slack.getMessageId());
        assertEquals("user1", slack.getUser());
        assertEquals("Hello world", slack.getText());
        assertEquals("2023-01-01T00:00:00Z", slack.getTimestamp());
        assertEquals("team1", slack.getTeam());
        assertEquals("general", slack.getChannel());
        assertEquals("ref-123", slack.getRawReference());
    }

    @Test
    void testSlackDTOToString() {
        SlackDTO slack = new SlackDTO();
        slack.setTenantId("tenant-1");
        slack.setUser("user1");

        String result = slack.toString();
        assertTrue(result.contains("tenant-1"));
        assertTrue(result.contains("user1"));
    }

    @Test
    void testSlackDTOIsNotBlank() {
        SlackDTO slack = new SlackDTO();

        assertTrue(slack.isNotBlank("test"));
        assertFalse(slack.isNotBlank(""));
        assertFalse(slack.isNotBlank(null));
    }
    @Test
    void testSlackDTONullFields() {
        SlackDTO slack = new SlackDTO();
        assertNull(slack.getTenantId());
        assertNull(slack.getMessageId());
        assertNull(slack.getUser());
    }

    @Test
    void testEqualsSameObject() {
        SlackDTO slack = new SlackDTO();
        slack.setTenantId("tenant-1");
        assertEquals(slack, slack);
    }


    @Test
    void testNotEqualsDifferentTenantId() {
        SlackDTO s1 = new SlackDTO();
        s1.setTenantId("tenant-1");

        SlackDTO s2 = new SlackDTO();
        s2.setTenantId("tenant-2");

        assertNotEquals(s1, s2);
    }

    @Test
    void testNotEqualsDifferentClass() {
        SlackDTO slack = new SlackDTO();
        assertNotEquals(slack, "some string");
    }

    @Test
    void testNotEqualsNull() {
        SlackDTO slack = new SlackDTO();
        assertNotEquals(slack, null);
    }

    @Test
    void testHashCodeConsistency() {
        SlackDTO slack = new SlackDTO();
        slack.setTenantId("tenant-1");
        int hash1 = slack.hashCode();
        int hash2 = slack.hashCode();
        assertEquals(hash1, hash2);
    }

    @Test
    void testToStringContainsAllFields() {
        SlackDTO slack = new SlackDTO();
        slack.setTenantId("tenant-1");
        slack.setMessageId("msg-1");
        slack.setText("Hello world");
        slack.setChannel("general");

        String result = slack.toString();
        assertTrue(result.contains("tenant-1"));
        assertTrue(result.contains("msg-1"));
        assertTrue(result.contains("Hello world"));
        assertTrue(result.contains("general"));
    }

    @Test
    void testIsNotBlankWhitespace() {
        SlackDTO slack = new SlackDTO();
        assertFalse(slack.isNotBlank("   "));
    }

}
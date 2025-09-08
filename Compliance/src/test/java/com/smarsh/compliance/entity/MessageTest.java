package com.smarsh.compliance.entity;


import com.smarsh.compliance.models.Message;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MessageTest {

    @Test
    void testMessageBuilder_CreatesCompleteObject() {
        Message message = Message.builder()
                .messageId("msg-1")
                .tenantId("tenant-1")
                .network("email")
                .timestamp(Instant.now())
                .flagged(true)
                .content(Message.Content.builder()
                        .subject("Test")
                        .body("Content")
                        .build())
                .participants(List.of(
                        Message.Participant.builder()
                                .role("sender")
                                .id("user@example.com")
                                .build()
                ))
                .context(Message.Context.builder()
                        .team("Test Team")
                        .channel("general")
                        .build())
                .build();

        assertEquals("msg-1", message.getMessageId());
        assertEquals("tenant-1", message.getTenantId());
        assertEquals("email", message.getNetwork());
        assertTrue(message.isFlagged());
        assertNotNull(message.getContent());
        assertNotNull(message.getParticipants());
        assertNotNull(message.getContext());
    }

    @Test
    void testMessageBuilder_WithNullValues() {
        Message message = Message.builder().build();

        assertNull(message.getMessageId());
        assertNull(message.getTenantId());
        assertNull(message.getNetwork());
        assertNull(message.getTimestamp());
        assertFalse(message.isFlagged());
        assertNull(message.getContent());
        assertNull(message.getParticipants());
        assertNull(message.getContext());
        assertNull(message.getFlagInfo());
    }
    // 1. Test setters properly update all fields
    @Test
    void testSetters_UpdateAllFields() {
        Message message = new Message();
        Message.Content content = Message.Content.builder().subject("Subj").body("Body").build();
        Message.Participant participant = Message.Participant.builder().role("sender").id("user@example.com").build();
        Message.Context context = Message.Context.builder().team("Team").channel("Channel").build();

        message.setMessageId("msg-2");
        message.setTenantId("tenant-2");
        message.setNetwork("sms");
        message.setTimestamp(Instant.now());
        message.setFlagged(true);
        message.setContent(content);
        message.setParticipants(List.of(participant));
        message.setContext(context);

        assertEquals("msg-2", message.getMessageId());
        assertEquals("tenant-2", message.getTenantId());
        assertEquals("sms", message.getNetwork());
        assertTrue(message.isFlagged());
        assertEquals(content, message.getContent());
        assertEquals(1, message.getParticipants().size());
        assertEquals(context, message.getContext());
    }

    // 2. Test equality for two identical messages
    @Test
    void testEquals_SameFields_ReturnsTrue() {
        Instant now = Instant.now();
        Message msg1 = Message.builder().messageId("msg-1").tenantId("tenant-1").timestamp(now).build();
        Message msg2 = Message.builder().messageId("msg-1").tenantId("tenant-1").timestamp(now).build();

        assertEquals(msg1.getMessageId(), msg2.getMessageId());
        assertEquals(msg1.getTenantId(), msg2.getTenantId());
        assertEquals(msg1.getTimestamp(), msg2.getTimestamp());
    }

    // 3. Test inequality for different messages
    @Test
    void testEquals_DifferentFields_ReturnsFalse() {
        Message msg1 = Message.builder().messageId("msg-1").tenantId("tenant-1").build();
        Message msg2 = Message.builder().messageId("msg-2").tenantId("tenant-2").build();

        assertNotEquals(msg1.getMessageId(), msg2.getMessageId());
        assertNotEquals(msg1.getTenantId(), msg2.getTenantId());
    }

    // 4. Test toString contains key fields
    @Test
    void testToString_ContainsFields() {
        Message message = Message.builder().messageId("msg-3").tenantId("tenant-3").network("email").build();
        String str = message.toString();

        assertTrue(str.contains("msg-3"));
        assertTrue(str.contains("tenant-3"));
        assertTrue(str.contains("email"));
    }

    // 5. Test Message with empty participants and null content/context
    @Test
    void testMessage_EmptyParticipants_NullContentAndContext() {
        Message message = Message.builder()
                .participants(List.of())
                .content(null)
                .context(null)
                .build();

        assertNotNull(message.getParticipants());
        assertEquals(0, message.getParticipants().size());
        assertNull(message.getContent());
        assertNull(message.getContext());
    }
}
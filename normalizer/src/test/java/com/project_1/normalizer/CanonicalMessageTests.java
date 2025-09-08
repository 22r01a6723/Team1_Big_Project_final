package com.project_1.normalizer;

import com.project_1.normalizer.model.CanonicalMessage;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CanonicalMessageTest {

    @Test
    void testCanonicalMessageBuilder() {
        Instant timestamp = Instant.now();
        CanonicalMessage message = CanonicalMessage.builder()
                .messageId("msg-1")
                .tenantId("tenant-1")
                .network("email")
                .timestamp(timestamp)
                .participants(List.of(
                        CanonicalMessage.Participant.builder()
                                .role("sender")
                                .id("user@example.com")
                                .displayName("User")
                                .build()
                ))
                .content(CanonicalMessage.Content.builder()
                        .subject("Test")
                        .body("Content")
                        .build())
                .context(CanonicalMessage.Context.builder()
                        .team("team-1")
                        .channel("general")
                        .rawReference("ref-123")
                        .build())
                .build();

        assertEquals("msg-1", message.getMessageId());
        assertEquals("tenant-1", message.getTenantId());
        assertEquals("email", message.getNetwork());
        assertEquals(timestamp, message.getTimestamp());
        assertEquals(1, message.getParticipants().size());
        assertEquals("Test", message.getContent().getSubject());
        assertEquals("team-1", message.getContext().getTeam());
    }

    @Test
    void testCanonicalMessageNoArgsConstructor() {
        CanonicalMessage message = new CanonicalMessage();

        assertNull(message.getMessageId());
        assertNull(message.getTenantId());
        assertNull(message.getNetwork());
        assertNull(message.getTimestamp());
        assertNull(message.getParticipants());
        assertNull(message.getContent());
        assertNull(message.getContext());
        assertNull(message.getCreatedAt());
    }

    @Test
    void testParticipantBuilder() {
        CanonicalMessage.Participant participant = CanonicalMessage.Participant.builder()
                .role("sender")
                .id("user@example.com")
                .displayName("User")
                .build();

        assertEquals("sender", participant.getRole());
        assertEquals("user@example.com", participant.getId());
        assertEquals("User", participant.getDisplayName());
    }

    @Test
    void testContentBuilder() {
        CanonicalMessage.Content content = CanonicalMessage.Content.builder()
                .subject("Test")
                .body("Content")
                .build();

        assertEquals("Test", content.getSubject());
        assertEquals("Content", content.getBody());
    }

    @Test
    void testContextBuilder() {
        CanonicalMessage.Context context = CanonicalMessage.Context.builder()
                .team("team-1")
                .channel("general")
                .rawReference("ref-123")
                .build();

        assertEquals("team-1", context.getTeam());
        assertEquals("general", context.getChannel());
        assertEquals("ref-123", context.getRawReference());
    }

    // ---- Extra 10 test cases ----

    @Test
    void testCanonicalMessageSettersAndGetters() {
        CanonicalMessage message = new CanonicalMessage();
        Instant now = Instant.now();
        message.setMessageId("id-1");
        message.setTenantId("tenant-1");
        message.setNetwork("chat");
        message.setTimestamp(now);
        message.setCreatedAt(now);

        assertEquals("id-1", message.getMessageId());
        assertEquals("tenant-1", message.getTenantId());
        assertEquals("chat", message.getNetwork());
        assertEquals(now, message.getTimestamp());
        assertEquals(now, message.getCreatedAt());
    }

    @Test
    void testParticipantSettersAndGetters() {
        CanonicalMessage.Participant participant = new CanonicalMessage.Participant();
        participant.setRole("receiver");
        participant.setId("id-123");
        participant.setDisplayName("Receiver User");

        assertEquals("receiver", participant.getRole());
        assertEquals("id-123", participant.getId());
        assertEquals("Receiver User", participant.getDisplayName());
    }

    @Test
    void testContentSettersAndGetters() {
        CanonicalMessage.Content content = new CanonicalMessage.Content();
        content.setSubject("Hello");
        content.setBody("World");

        assertEquals("Hello", content.getSubject());
        assertEquals("World", content.getBody());
    }

    @Test
    void testContextSettersAndGetters() {
        CanonicalMessage.Context context = new CanonicalMessage.Context();
        context.setTeam("alpha");
        context.setChannel("random");
        context.setRawReference("xyz-123");

        assertEquals("alpha", context.getTeam());
        assertEquals("random", context.getChannel());
        assertEquals("xyz-123", context.getRawReference());
    }

    @Test
    void testEqualsAndHashCode_SameMessage() {
        Instant now = Instant.now();
        CanonicalMessage m1 = CanonicalMessage.builder().messageId("m1").timestamp(now).build();
        CanonicalMessage m2 = CanonicalMessage.builder().messageId("m1").timestamp(now).build();

        assertEquals(m1, m2);
        assertEquals(m1.hashCode(), m2.hashCode());
    }

    @Test
    void testEquals_DifferentMessageId() {
        CanonicalMessage m1 = CanonicalMessage.builder().messageId("m1").build();
        CanonicalMessage m2 = CanonicalMessage.builder().messageId("m2").build();

        assertNotEquals(m1, m2);
    }

    @Test
    void testEquals_WithNull() {
        CanonicalMessage message = CanonicalMessage.builder().messageId("m1").build();
        assertNotEquals(message, null);
    }

    @Test
    void testEquals_DifferentClass() {
        CanonicalMessage message = CanonicalMessage.builder().messageId("m1").build();
        assertNotEquals(message, "not-a-message");
    }

    @Test
    void testToStringContainsFields() {
        CanonicalMessage message = CanonicalMessage.builder()
                .messageId("m1")
                .tenantId("tenant-x")
                .network("slack")
                .build();

        String str = message.toString();
        assertTrue(str.contains("m1"));
        assertTrue(str.contains("tenant-x"));
        assertTrue(str.contains("slack"));
    }

    @Test
    void testBuilderWithNullParticipants() {
        CanonicalMessage message = CanonicalMessage.builder()
                .messageId("m2")
                .participants(null)
                .build();

        assertNull(message.getParticipants());
    }
}

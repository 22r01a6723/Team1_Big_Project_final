package com.complyvault.retention.message;


import com.complyvault.retention.model.CanonicalMessage;
import org.junit.jupiter.api.Test;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CanonicalMessageTest {

    @Test
    void canonicalMessageBuilder_ShouldCreateValidObject() {
        // Arrange
        CanonicalMessage.Participant participant = CanonicalMessage.Participant.builder()
                .role("sender")
                .id("user-123")
                .displayName("John Doe")
                .build();

        CanonicalMessage.Content content = CanonicalMessage.Content.builder()
                .subject("Test Subject")
                .body("Test Body")
                .build();

        CanonicalMessage.Context context = CanonicalMessage.Context.builder()
                .team("team-1")
                .channel("general")
                .rawReference("ref-123")
                .build();

        // Act
        CanonicalMessage message = CanonicalMessage.builder()
                .messageId("msg-123")
                .tenantId("tenant-1")
                .network("slack")
                .timestamp(Instant.now())
                .participants(Arrays.asList(participant))
                .content(content)
                .context(context)
                .expired(false)
                .build();

        // Assert
        assertNotNull(message);
        assertEquals("msg-123", message.getMessageId());
        assertEquals("tenant-1", message.getTenantId());
        assertEquals("slack", message.getNetwork());
        assertFalse(message.getExpired());
        assertEquals(1, message.getParticipants().size());
        assertEquals("Test Subject", message.getContent().getSubject());
        assertEquals("general", message.getContext().getChannel());
    }

    @Test
    void participantBuilder_ShouldCreateValidParticipant() {
        // Act
        CanonicalMessage.Participant participant = CanonicalMessage.Participant.builder()
                .role("receiver")
                .id("user-456")
                .displayName("Jane Smith")
                .build();

        // Assert
        assertEquals("receiver", participant.getRole());
        assertEquals("user-456", participant.getId());
        assertEquals("Jane Smith", participant.getDisplayName());
    }

    @Test
    void contentBuilder_ShouldCreateValidContent() {
        // Act
        CanonicalMessage.Content content = CanonicalMessage.Content.builder()
                .subject("Important Message")
                .body("This is an important message body")
                .build();

        // Assert
        assertEquals("Important Message", content.getSubject());
        assertEquals("This is an important message body", content.getBody());
    }

    @Test
    void contextBuilder_ShouldCreateValidContext() {
        // Act
        CanonicalMessage.Context context = CanonicalMessage.Context.builder()
                .team("development")
                .channel("announcements")
                .rawReference("msg-ref-789")
                .build();

        // Assert
        assertEquals("development", context.getTeam());
        assertEquals("announcements", context.getChannel());
        assertEquals("msg-ref-789", context.getRawReference());
    }

    @Test
    void documentAnnotation_ShouldBePresent() {
        // Arrange
        Document document = CanonicalMessage.class.getAnnotation(org.springframework.data.mongodb.core.mapping.Document.class);

        // Assert
        assertNotNull(document);
        assertEquals("messages", document.collection());
    }

    @Test
    void idAnnotation_ShouldBeOnMessageIdField() throws NoSuchFieldException {
        // Arrange
        java.lang.reflect.Field field = CanonicalMessage.class.getDeclaredField("messageId");
        org.springframework.data.annotation.Id idAnnotation = field.getAnnotation(org.springframework.data.annotation.Id.class);

        // Assert
        assertNotNull(idAnnotation);
    }

    @Test
    void equalsAndHashCode_ShouldWorkCorrectly() {
        // Arrange
        CanonicalMessage message1 = CanonicalMessage.builder()
                .messageId("msg-1")
                .tenantId("tenant-1")
                .build();

        CanonicalMessage message2 = CanonicalMessage.builder()
                .messageId("msg-1")
                .tenantId("tenant-1")
                .build();

        CanonicalMessage message3 = CanonicalMessage.builder()
                .messageId("msg-2")
                .tenantId("tenant-1")
                .build();

        // Assert
        assertEquals(message1, message2);
        assertNotEquals(message1, message3);
        assertEquals(message1.hashCode(), message2.hashCode());
    }

    @Test
    void toString_ShouldContainKeyFields() {
        // Arrange
        CanonicalMessage message = CanonicalMessage.builder()
                .messageId("msg-999")
                .tenantId("tenant-xyz")
                .network("teams")
                .build();

        // Act
        String str = message.toString();

        // Assert
        assertTrue(str.contains("msg-999"));
        assertTrue(str.contains("tenant-xyz"));
        assertTrue(str.contains("teams"));
    }

    @Test
    void builder_ShouldHandleNullOptionalFields() {
        // Act
        CanonicalMessage message = CanonicalMessage.builder()
                .messageId("msg-null")
                .tenantId("tenant-null")
                .build();

        // Assert
        assertNotNull(message);
        assertEquals("msg-null", message.getMessageId());
        assertNull(message.getContent());
        assertNull(message.getContext());
        assertNull(message.getParticipants());
    }

    @Test
    void participantsList_ShouldBeMutable() {
        // Arrange
        CanonicalMessage.Participant participant = CanonicalMessage.Participant.builder()
                .id("user-789")
                .role("observer")
                .build();

        CanonicalMessage message = CanonicalMessage.builder()
                .messageId("msg-555")
                .participants(new java.util.ArrayList<>())
                .build();

        // Act
        message.getParticipants().add(participant);

        // Assert
        assertEquals(1, message.getParticipants().size());
        assertEquals("observer", message.getParticipants().get(0).getRole());
    }

    @Test
    void expiredFlag_ShouldBeSetCorrectly() {
        // Act
        CanonicalMessage expiredMessage = CanonicalMessage.builder()
                .messageId("msg-expired")
                .expired(true)
                .build();

        CanonicalMessage activeMessage = CanonicalMessage.builder()
                .messageId("msg-active")
                .expired(false)
                .build();

        // Assert
        assertTrue(expiredMessage.getExpired());
        assertFalse(activeMessage.getExpired());
    }

    @Test
    void timestamp_ShouldBeStoredCorrectly() {
        // Arrange
        Instant now = Instant.now();

        CanonicalMessage message = CanonicalMessage.builder()
                .messageId("msg-time")
                .timestamp(now)
                .build();

        // Assert
        assertEquals(now, message.getTimestamp());
    }

}
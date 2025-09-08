package com.smarsh.compliance.kafka;


import com.smarsh.compliance.models.Message;
import com.smarsh.compliance.service.AuditService;
import com.smarsh.compliance.service.ComplianceService;
import com.smarsh.compliance.service.MessageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MessageConsumerTest {

    @Mock
    private ComplianceService complianceService;

    @Mock
    private MessageService messageService;

    @Mock
    private AuditService auditService;

    private MessageConsumer messageConsumer;

    @BeforeEach
    void setUp() {
        messageConsumer = new MessageConsumer(complianceService, auditService, messageService);
    }

    @Test
    void testConsume_ValidMessage_ProcessesAndPublishes() {
        Message message = createTestMessage();
        Message processedMessage = createTestMessage();
        processedMessage.setFlagged(true);

        when(complianceService.process(message)).thenReturn(processedMessage);
        doNothing().when(messageService).publishMessage(processedMessage);

        messageConsumer.consume(message);

        verify(auditService).logEvent(eq("tenant-1"), eq("msg-1"), eq("email"),
                eq("MESSAGE_RECEIVED_FROM_KAFKA"), any(Map.class));
        verify(complianceService).process(message);
        verify(messageService).publishMessage(processedMessage);
        verify(auditService).logEvent(eq("tenant-1"), eq("msg-1"), eq("email"),
                eq("MESSAGE_PUBLISHED_TO_KAFKA"), any(Map.class));
    }

    @Test
    void testConsume_ProcessingException_LogsError() {
        Message message = createTestMessage();

        when(complianceService.process(message)).thenThrow(new RuntimeException("Processing error"));

        assertDoesNotThrow(() -> messageConsumer.consume(message));

        verify(auditService).logEvent(eq("tenant-1"), eq("msg-1"), eq("email"),
                eq("MESSAGE_RECEIVED_FROM_KAFKA"), any(Map.class));
        verify(auditService).logEvent(eq("tenant-1"), eq("msg-1"), eq("email"),
                eq("Processed Message published to kafka"), any(Map.class));
        verify(messageService, never()).publishMessage(any());
    }

    @Test
    void testConsume_FlaggedMessage_RemainsFlagged() {
        Message message = createTestMessage();
        message.setFlagged(true);

        when(complianceService.process(message)).thenReturn(message);

        assertDoesNotThrow(() -> messageConsumer.consume(message));

        verify(messageService).publishMessage(message);
        assert(message.isFlagged());
    }

    // 2. Test multiple consume calls do not interfere
    @Test
    void testConsume_MultipleMessages() {
        Message msg1 = createTestMessage();
        Message msg2 = createTestMessage();
        msg2.setMessageId("msg-2");

        when(complianceService.process(msg1)).thenReturn(msg1);
        when(complianceService.process(msg2)).thenReturn(msg2);

        assertDoesNotThrow(() -> {
            messageConsumer.consume(msg1);
            messageConsumer.consume(msg2);
        });

        verify(messageService).publishMessage(msg1);
        verify(messageService).publishMessage(msg2);
    }

    // 3. Test consume message with empty content still publishes
    @Test
    void testConsume_EmptyContentMessage() {
        Message message = createTestMessage();
        message.getContent().setSubject("");
        message.getContent().setBody("");

        when(complianceService.process(message)).thenReturn(message);

        assertDoesNotThrow(() -> messageConsumer.consume(message));

        verify(messageService).publishMessage(message);
    }

    // 4. Test audit logs are always called even if content is missing
    @Test
    void testConsume_AuditLogsAlwaysCalled() {
        Message message = createTestMessage();
        message.setContent(null);

        when(complianceService.process(message)).thenReturn(message);

        assertDoesNotThrow(() -> messageConsumer.consume(message));

        verify(auditService).logEvent(eq("tenant-1"), eq("msg-1"), eq("email"),
                eq("MESSAGE_RECEIVED_FROM_KAFKA"), any(Map.class));
        verify(auditService).logEvent(eq("tenant-1"), eq("msg-1"), eq("email"),
                eq("MESSAGE_PUBLISHED_TO_KAFKA"), any(Map.class));
    }

    @Test
    void testConsume_MessageWithNullFields_StillProcesses() {
        Message message = createTestMessage();
        message.setTenantId(null);
        message.setMessageId(null);
        message.setNetwork(null);

        Message processedMessage = createTestMessage();
        when(complianceService.process(message)).thenReturn(processedMessage);

        assertDoesNotThrow(() -> messageConsumer.consume(message));

        verify(complianceService).process(message);
        verify(messageService).publishMessage(processedMessage);
    }

    // 5. Test consume transforms content during processing
    @Test
    void testConsume_ContentTransformation() {
        Message message = createTestMessage();
        Message transformedMessage = createTestMessage();
        transformedMessage.getContent().setBody("Transformed content");

        when(complianceService.process(message)).thenReturn(transformedMessage);

        assertDoesNotThrow(() -> messageConsumer.consume(message));

        verify(messageService).publishMessage(transformedMessage);
        assert(transformedMessage.getContent().getBody().equals("Transformed content"));
    }

    private Message createTestMessage() {
        return Message.builder()
                .messageId("msg-1")
                .tenantId("tenant-1")
                .network("email")
                .timestamp(Instant.now())
                .content(Message.Content.builder()
                        .subject("Test")
                        .body("Content")
                        .build())
                .build();
    }
}
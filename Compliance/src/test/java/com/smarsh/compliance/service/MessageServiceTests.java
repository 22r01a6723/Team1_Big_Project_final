package com.smarsh.compliance.service;


import com.smarsh.compliance.models.Message;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MessageServiceTest {

    @Mock
    private KafkaProducerService kafkaProducerService;

    private MessageService messageService;

    @BeforeEach
    void setUp() {
        messageService = new MessageService(kafkaProducerService);
    }

    @Test
    void testPublishMessage_CallsKafkaProducer() {
        Message message = createTestMessage();

        messageService.publishMessage(message);

        verify(kafkaProducerService).publishMessage(message);
    }

    @Test
    void testPublishMessage_NullMessage_DoesNotThrow() {
        assertDoesNotThrow(() -> messageService.publishMessage(null));
    }

    @Test
    void testPublishMessage_MessageWithNullId_DoesNotThrow() {
        Message message = createTestMessage();
        message.setMessageId(null);

        assertDoesNotThrow(() -> messageService.publishMessage(message));
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
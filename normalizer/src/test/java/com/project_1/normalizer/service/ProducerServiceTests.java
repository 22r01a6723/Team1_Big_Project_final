package com.project_1.normalizer.service;


import com.project_1.normalizer.kafka.ComplianceProducer;
import com.project_1.normalizer.model.CanonicalMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProducerServiceTest {

    @Mock
    private ComplianceProducer complianceProducer;

    private ProducerService producerService;

    @BeforeEach
    void setUp() {
        producerService = new ProducerService(complianceProducer);
    }

    @Test
    void testSendMessage_WithOnlyTenantId() {
        CanonicalMessage message = CanonicalMessage.builder()
                .tenantId("tenant-1")
                .build();

        producerService.sendMessage(message);

        verify(complianceProducer).sendMessage(message);
    }

    @Test
    void testSendMessage_WithFullMessageDetails() {
        CanonicalMessage message = CanonicalMessage.builder()
                .messageId("msg-2")
                .tenantId("tenant-2")
                .network("email")
                .timestamp(Instant.ofEpochSecond(System.currentTimeMillis()))
                .build();

        producerService.sendMessage(message);

        verify(complianceProducer).sendMessage(message);
    }

    @Test
    void testSendMessage_CalledTwiceForSameMessage() {
        CanonicalMessage message = CanonicalMessage.builder().messageId("msg-3").build();

        producerService.sendMessage(message);
        producerService.sendMessage(message);

        verify(complianceProducer, times(2)).sendMessage(message);
    }

    @Test
    void testSendMessage_DifferentMessagesSentSeparately() {
        CanonicalMessage msg1 = CanonicalMessage.builder().messageId("msg-4a").build();
        CanonicalMessage msg2 = CanonicalMessage.builder().messageId("msg-4b").build();

        producerService.sendMessage(msg1);
        producerService.sendMessage(msg2);

        verify(complianceProducer).sendMessage(msg1);
        verify(complianceProducer).sendMessage(msg2);
    }

    @Test
    void testSendMessage_ProducerThrowsCheckedException() {
        CanonicalMessage message = CanonicalMessage.builder().messageId("msg-5").build();

        doThrow(new IllegalArgumentException("Bad data")).when(complianceProducer).sendMessage(any());

        assertDoesNotThrow(() -> producerService.sendMessage(message));
        verify(complianceProducer).sendMessage(message);
    }

    @Test
    void testSendMessage_ProducerThrowsMultipleTimes() {
        CanonicalMessage message = CanonicalMessage.builder().messageId("msg-6").build();

        doThrow(new RuntimeException("Kafka error")).when(complianceProducer).sendMessage(any());

        assertDoesNotThrow(() -> {
            producerService.sendMessage(message);
            producerService.sendMessage(message);
        });

        verify(complianceProducer, times(2)).sendMessage(message);
    }

    @Test
    void testSendMessage_WithEmptyMessageId() {
        CanonicalMessage message = CanonicalMessage.builder().messageId("").build();

        producerService.sendMessage(message);

        verify(complianceProducer).sendMessage(message);
    }

    @Test
    void testSendMessage_WithNullFieldsInMessage() {
        CanonicalMessage message = CanonicalMessage.builder()
                .messageId("msg-7")
                .tenantId(null)
                .network(null)
                .build();

        producerService.sendMessage(message);

        verify(complianceProducer).sendMessage(message);
    }

    @Test
    void testSendMessage_MultipleDifferentMessagesInSequence() {
        for (int i = 1; i <= 5; i++) {
            CanonicalMessage msg = CanonicalMessage.builder().messageId("msg-" + i).build();
            producerService.sendMessage(msg);
        }

        verify(complianceProducer, times(5)).sendMessage(any(CanonicalMessage.class));
    }

    @Test
    void testSendMessage_WithVeryLargeMessageId() {
        String largeId = "x".repeat(10_000); // simulate huge payload
        CanonicalMessage message = CanonicalMessage.builder().messageId(largeId).build();

        producerService.sendMessage(message);

        verify(complianceProducer).sendMessage(message);
    }


    @Test
    void testSendMessage_Success() {
        CanonicalMessage message = CanonicalMessage.builder().messageId("msg-1").build();

        producerService.sendMessage(message);

        verify(complianceProducer).sendMessage(message);
    }

    @Test
    void testSendMessage_ProducerThrowsException_LogsError() {
        CanonicalMessage message = CanonicalMessage.builder().messageId("msg-1").build();

        doThrow(new RuntimeException("Kafka error")).when(complianceProducer).sendMessage(any());

        assertDoesNotThrow(() -> producerService.sendMessage(message));
        verify(complianceProducer).sendMessage(message);
    }

}
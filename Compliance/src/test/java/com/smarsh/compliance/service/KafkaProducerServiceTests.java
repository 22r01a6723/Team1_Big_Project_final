package com.smarsh.compliance.service;

import com.smarsh.compliance.models.Message;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class KafkaProducerServiceTest {

    @Mock
    private KafkaTemplate<String, Object> kafkaTemplate;

    private KafkaProducerService kafkaProducerService;

    @BeforeEach
    void setUp() {
        kafkaProducerService = new KafkaProducerService(kafkaTemplate);
    }

    @Test
    void testPublishMessage_Success() {
        Message message = createTestMessage();
        when(kafkaTemplate.send(any(), any(), any())).thenReturn(null); // no future

        assertDoesNotThrow(() -> kafkaProducerService.publishMessage(message));
        verify(kafkaTemplate).send("search-topic", "msg-1", message);
    }

    @Test
    void testPublishMessage_VerifyMultipleCalls() {
        Message message = createTestMessage();
        when(kafkaTemplate.send(any(), any(), any())).thenReturn(null);

        assertDoesNotThrow(() -> {
            kafkaProducerService.publishMessage(message);
            kafkaProducerService.publishMessage(message);
        });

        verify(kafkaTemplate, times(2)).send("search-topic", "msg-1", message);
    }

    @Test
    void testPublishMessage_WithDifferentKeys() {
        Message msg1 = createTestMessage();
        Message msg2 = createTestMessage();
        msg2.setMessageId("msg-2");
        when(kafkaTemplate.send(any(), any(), any())).thenReturn(null);

        kafkaProducerService.publishMessage(msg1);
        kafkaProducerService.publishMessage(msg2);

        verify(kafkaTemplate).send("search-topic", "msg-1", msg1);
        verify(kafkaTemplate).send("search-topic", "msg-2", msg2);
    }


    // Helper method
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

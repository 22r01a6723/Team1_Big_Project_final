package com.project_1.normalizer.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project_1.normalizer.model.CanonicalMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ComplianceProducerTests {

    @Mock
    private KafkaTemplate<String, String> kafkaTemplate;

    @Mock
    private ObjectMapper objectMapper;

    private ComplianceProducer complianceProducer;

    @BeforeEach
    void setUp() {
        complianceProducer = new ComplianceProducer(kafkaTemplate, objectMapper);
    }

    /** 1. ✅ Success path */
    @Test
    void testSendMessage_Success() throws JsonProcessingException {
        CanonicalMessage message = createTestMessage();
        when(objectMapper.writeValueAsString(message)).thenReturn("{\"messageId\":\"msg-1\"}");

        assertDoesNotThrow(() -> complianceProducer.sendMessage(message));

        verify(objectMapper).writeValueAsString(message);
        verify(kafkaTemplate).send(eq("compliance-topic"), eq("tenant-1"), anyString());
    }

    /** 2. ❌ JSON serialization error */
    @Test
    void testSendMessage_JsonProcessingException() throws JsonProcessingException {
        CanonicalMessage message = createTestMessage();
        when(objectMapper.writeValueAsString(message)).thenThrow(new JsonProcessingException("JSON error") {});

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> complianceProducer.sendMessage(message)
        );

        assertTrue(exception.getMessage().contains("Error serializing CanonicalMessage"));
        verify(objectMapper).writeValueAsString(message);
    }

    /** 3. ❌ Null message */
    @Test
    void testSendMessage_NullMessage() {
        assertThrows(NullPointerException.class, () -> complianceProducer.sendMessage(null));
    }

    /** 4. ✅ Verify correct topic */
    @Test
    void testSendMessage_UsesCorrectTopic() throws JsonProcessingException {
        CanonicalMessage message = createTestMessage();
        when(objectMapper.writeValueAsString(message)).thenReturn("{}");

        complianceProducer.sendMessage(message);

        verify(kafkaTemplate).send(eq("compliance-topic"), eq("tenant-1"), anyString());
    }

    /** 5. ✅ Verify correct key */
    @Test
    void testSendMessage_UsesTenantIdAsKey() throws JsonProcessingException {
        CanonicalMessage message = createTestMessage();
        when(objectMapper.writeValueAsString(message)).thenReturn("{}");

        complianceProducer.sendMessage(message);

        verify(kafkaTemplate).send(anyString(), eq("tenant-1"), anyString());
    }

    /** 6. ✅ Verify message body contains JSON */
    @Test
    void testSendMessage_MessageBodySerialized() throws JsonProcessingException {
        CanonicalMessage message = createTestMessage();
        String json = "{\"messageId\":\"msg-1\"}";
        when(objectMapper.writeValueAsString(message)).thenReturn(json);

        complianceProducer.sendMessage(message);

        verify(kafkaTemplate).send(eq("compliance-topic"), eq("tenant-1"), eq(json));
    }

    /** 7. ❌ Empty tenantId */
    @Test
    void testSendMessage_EmptyTenantIdStillSends() throws JsonProcessingException {
        CanonicalMessage message = CanonicalMessage.builder()
                .messageId("msg-2")
                .tenantId("") // empty
                .network("slack")
                .timestamp(Instant.now())
                .build();

        when(objectMapper.writeValueAsString(message)).thenReturn("{\"messageId\":\"msg-2\"}");

        complianceProducer.sendMessage(message);

        verify(kafkaTemplate).send(eq("compliance-topic"), eq(""), anyString());
    }

    /** 8. ❌ Null tenantId */
    @Test
    void testSendMessage_NullTenantIdStillSends() throws JsonProcessingException {
        CanonicalMessage message = CanonicalMessage.builder()
                .messageId("msg-3")
                .tenantId(null) // null
                .network("teams")
                .timestamp(Instant.now())
                .build();

        when(objectMapper.writeValueAsString(message)).thenReturn("{\"messageId\":\"msg-3\"}");

        complianceProducer.sendMessage(message);

        verify(kafkaTemplate).send(eq("compliance-topic"), isNull(), anyString());
    }

    /** 9. ✅ Different network type */
    @Test
    void testSendMessage_WithDifferentNetwork() throws JsonProcessingException {
        CanonicalMessage message = CanonicalMessage.builder()
                .messageId("msg-4")
                .tenantId("tenant-2")
                .network("slack")
                .timestamp(Instant.now())
                .build();

        when(objectMapper.writeValueAsString(message)).thenReturn("{\"messageId\":\"msg-4\"}");

        complianceProducer.sendMessage(message);

        verify(kafkaTemplate).send(eq("compliance-topic"), eq("tenant-2"), anyString());
    }

    /** 10. ✅ Very large payload */
    @Test
    void testSendMessage_LargePayload() throws JsonProcessingException {
        CanonicalMessage message = CanonicalMessage.builder()
                .messageId("msg-5")
                .tenantId("tenant-big")
                .network("email")
                .timestamp(Instant.now())
                .build();

        String bigJson = "{\"data\":\"" + "x".repeat(5000) + "\"}";
        when(objectMapper.writeValueAsString(message)).thenReturn(bigJson);

        complianceProducer.sendMessage(message);

        verify(kafkaTemplate).send(eq("compliance-topic"), eq("tenant-big"), eq(bigJson));
    }

    /** 11. ✅ Multiple calls */
    @Test
    void testSendMessage_CalledTwice() throws JsonProcessingException {
        CanonicalMessage message = createTestMessage();
        when(objectMapper.writeValueAsString(message)).thenReturn("{\"messageId\":\"msg-1\"}");

        complianceProducer.sendMessage(message);
        complianceProducer.sendMessage(message);

        verify(kafkaTemplate, times(2)).send(eq("compliance-topic"), eq("tenant-1"), anyString());
    }

    /** 12. ❌ ObjectMapper returns null JSON */
    @Test
    void testSendMessage_ObjectMapperReturnsNull() throws JsonProcessingException {
        CanonicalMessage message = createTestMessage();
        when(objectMapper.writeValueAsString(message)).thenReturn(null);

        complianceProducer.sendMessage(message);

        verify(kafkaTemplate).send(eq("compliance-topic"), eq("tenant-1"), isNull());
    }

    private CanonicalMessage createTestMessage() {
        return CanonicalMessage.builder()
                .messageId("msg-1")
                .tenantId("tenant-1")
                .network("email")
                .timestamp(Instant.now())
                .build();
    }
}

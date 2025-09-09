package com.Project1.IngestionAndValidation.message;

import com.complyvault.shared.client.AuditClient;
import com.Project1.IngestionAndValidation.utils.MessageIdGenerator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MessageIdGeneratorTests {

    @Mock
    private AuditClient auditClient;

    private MessageIdGenerator messageIdGenerator;
    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        messageIdGenerator = new MessageIdGenerator(auditClient);
    }

    @Test
    void testGenerate_WithSimpleJson_ReturnsNonNull() throws Exception {
        JsonNode payload = objectMapper.readTree("{\"field\":\"value\"}");
        String id = messageIdGenerator.generate(payload);
        assertNotNull(id);
    }

    @Test
    void testGenerate_SameJson_ReturnsSameId() throws Exception {
        JsonNode payload = objectMapper.readTree("{\"field\":\"value\"}");
        String id1 = messageIdGenerator.generate(payload);
        String id2 = messageIdGenerator.generate(payload);
        assertEquals(id1, id2);
    }

    @Test
    void testGenerate_DifferentJson_ReturnsDifferentId() throws Exception {
        JsonNode payload1 = objectMapper.readTree("{\"field\":\"value1\"}");
        JsonNode payload2 = objectMapper.readTree("{\"field\":\"value2\"}");
        assertNotEquals(messageIdGenerator.generate(payload1), messageIdGenerator.generate(payload2));
    }

    @Test
    void testGenerate_EmptyJson_ReturnsNonNull() throws Exception {
        JsonNode payload = objectMapper.readTree("{}");
        String id = messageIdGenerator.generate(payload);
        assertNotNull(id);
    }

    @Test
    void testGenerate_JsonWithNestedFields_ReturnsNonNull() throws Exception {
        JsonNode payload = objectMapper.readTree("{\"outer\":{\"inner\":\"value\"}}");
        String id = messageIdGenerator.generate(payload);
        assertNotNull(id);
    }

    @Test
    void testGenerate_JsonWithArray_ReturnsNonNull() throws Exception {
        JsonNode payload = objectMapper.readTree("{\"array\":[1,2,3]}");
        String id = messageIdGenerator.generate(payload);
        assertNotNull(id);
    }


    @Test
    void testGenerate_AuditClientCalled() throws Exception {
        JsonNode payload = objectMapper.readTree("{\"field\":\"value\"}");
        messageIdGenerator.generate(payload);
        verify(auditClient, atLeastOnce()).logEvent(any(), any(), any(), any(), any(), any());
    }

    @Test
    void testGenerate_SameJsonTwice_AuditCalledTwice() throws Exception {
        JsonNode payload = objectMapper.readTree("{\"field\":\"value\"}");
        messageIdGenerator.generate(payload);
        messageIdGenerator.generate(payload);
        verify(auditClient, times(2)).logEvent(any(), any(), any(), any(), any(), any());
    }

    @Test
    void testGenerate_LargeJson_ReturnsNonNull() throws Exception {
        String largeJson = "{\"field1\":\"value1\",\"field2\":\"value2\",\"field3\":\"value3\"}";
        JsonNode payload = objectMapper.readTree(largeJson);
        String id = messageIdGenerator.generate(payload);
        assertNotNull(id);
    }
}

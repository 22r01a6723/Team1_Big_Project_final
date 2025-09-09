package com.Project1.Search.service;


import com.Project1.Search.model.Message;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MessageServiceTest {

    private MessageService messageService;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        messageService = new MessageService();
        objectMapper = new ObjectMapper();
    }

    @Test
    void testMapToMessageWithNullFlagInfo() throws Exception {
        String payload = "{\"messageId\":\"msg1\",\"tenantId\":\"tenant1\",\"timestamp\":1640995200,\"network\":\"slack\",\"flagged\":false,\"createdAt\":1640995200,\"participants\":[],\"content\":{\"subject\":\"Test\",\"body\":\"Hello\"},\"context\":{\"team\":\"team1\",\"channel\":\"general\"},\"flagInfo\":null}";

        Message message = messageService.MapToMessage(payload);

        assertNull(message.getFlagInfo());
    }

    @Test
    void testMapToMessageWithFlagInfo() throws Exception {
        String payload = "{\"messageId\":\"msg1\",\"tenantId\":\"tenant1\",\"timestamp\":1640995200,\"network\":\"slack\",\"flagged\":true,\"createdAt\":1640995200,\"participants\":[],\"content\":{\"subject\":\"Test\",\"body\":\"Hello\"},\"context\":{\"team\":\"team1\",\"channel\":\"general\"},\"flagInfo\":{\"flagDescription\":\"Suspicious\",\"timestamp\":1640995200}}";

        Message message = messageService.MapToMessage(payload);

        assertTrue(message.isFlagged());
        assertNotNull(message.getFlagInfo());
        assertEquals("Suspicious", message.getFlagInfo().getFlagDescription());
    }

    @Test
    void testMapToMessageInvalidJson() {
        String invalidPayload = "invalid json";

        assertThrows(Exception.class, () -> {
            messageService.MapToMessage(invalidPayload);
        });
    }

}
package com.Project1.Search.consumer;

import com.Project1.Search.model.Message;
import com.Project1.Search.repository.MessageRepository;
import com.Project1.Search.service.MessageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class MessageConsumerTest {

    private MessageRepository messageRepository;
    private MessageService messageService;
    private MessageConsumer messageConsumer;

    @BeforeEach
    void setUp() {
        messageRepository = Mockito.mock(MessageRepository.class);
        messageService = Mockito.mock(MessageService.class);
        messageConsumer = new MessageConsumer(messageRepository, messageService);
    }

    @Test
    void testConsumeValidMessage() throws Exception {
        String validJson = "{\"messageId\":\"msg1\",\"tenantId\":\"tenant1\",\"timestamp\":1640995200,\"flagged\":false}";
        Message mockMessage = Message.builder().messageId("msg1").tenantId("tenant1").build();

        when(messageService.MapToMessage(validJson)).thenReturn(mockMessage);

        messageConsumer.consume(validJson);

        verify(messageRepository, times(1)).save(mockMessage);
    }

    @Test
    void testConsumeInvalidJson() throws Exception {
        String invalidJson = "invalid json";

        when(messageService.MapToMessage(invalidJson)).thenThrow(new RuntimeException("Invalid JSON"));

        messageConsumer.consume(invalidJson);

        verify(messageRepository, never()).save(any());
    }

    @Test
    void testConsumeMessageWithNullFields() throws Exception {
        String jsonWithNulls = "{\"messageId\":null,\"tenantId\":\"tenant1\"}";
        Message mockMessage = Message.builder().messageId(null).tenantId("tenant1").build();

        when(messageService.MapToMessage(jsonWithNulls)).thenReturn(mockMessage);

        messageConsumer.consume(jsonWithNulls);

        verify(messageRepository, times(1)).save(mockMessage);
    }

    @Test
    void testConsumeMultipleMessages() throws Exception {
        String json1 = "{\"messageId\":\"msg1\",\"tenantId\":\"tenant1\"}";
        String json2 = "{\"messageId\":\"msg2\",\"tenantId\":\"tenant1\"}";
        Message msg1 = Message.builder().messageId("msg1").tenantId("tenant1").build();
        Message msg2 = Message.builder().messageId("msg2").tenantId("tenant1").build();

        when(messageService.MapToMessage(json1)).thenReturn(msg1);
        when(messageService.MapToMessage(json2)).thenReturn(msg2);

        messageConsumer.consume(json1);
        messageConsumer.consume(json2);

        verify(messageRepository, times(1)).save(msg1);
        verify(messageRepository, times(1)).save(msg2);
    }

    @Test
    void testConsumeMessageWithServiceException() throws Exception {
        String json = "{\"messageId\":\"msg1\"}";

        when(messageService.MapToMessage(json)).thenThrow(new RuntimeException("Service error"));

        messageConsumer.consume(json);

        verify(messageRepository, never()).save(any());
    }
    @Test
    void testConsumeEmptyJsonString() throws Exception {
        String emptyJson = "";

        when(messageService.MapToMessage(emptyJson)).thenThrow(new RuntimeException("Empty JSON"));

        messageConsumer.consume(emptyJson);

        verify(messageRepository, never()).save(any());
    }

    @Test
    void testConsumeNullJsonString() throws Exception {
        String nullJson = null;

        when(messageService.MapToMessage(nullJson)).thenThrow(new RuntimeException("Null JSON"));

        messageConsumer.consume(nullJson);

        verify(messageRepository, never()).save(any());
    }

    @Test
    void testConsumeMessageRepositoryThrowsException() throws Exception {
        String json = "{\"messageId\":\"msg1\",\"tenantId\":\"tenant1\"}";
        Message mockMessage = Message.builder().messageId("msg1").tenantId("tenant1").build();

        when(messageService.MapToMessage(json)).thenReturn(mockMessage);
        doThrow(new RuntimeException("DB error")).when(messageRepository).save(mockMessage);

        assertDoesNotThrow(() -> messageConsumer.consume(json));
        verify(messageRepository, times(1)).save(mockMessage);
    }

    @Test
    void testConsumeMessageWithDuplicateMessageId() throws Exception {
        String json = "{\"messageId\":\"duplicate\",\"tenantId\":\"tenant1\"}";
        Message mockMessage = Message.builder().messageId("duplicate").tenantId("tenant1").build();

        when(messageService.MapToMessage(json)).thenReturn(mockMessage);
        when(messageRepository.save(mockMessage)).thenReturn(mockMessage);

        messageConsumer.consume(json);
        messageConsumer.consume(json);

        // Even if duplicate, should still attempt saving twice unless logic prevents it
        verify(messageRepository, times(2)).save(mockMessage);
    }

    @Test
    void testConsumeValidMessageCallsServiceOnce() throws Exception {
        String json = "{\"messageId\":\"msg1\",\"tenantId\":\"tenant1\"}";
        Message mockMessage = Message.builder().messageId("msg1").tenantId("tenant1").build();

        when(messageService.MapToMessage(json)).thenReturn(mockMessage);

        messageConsumer.consume(json);

        verify(messageService, times(1)).MapToMessage(json);
        verify(messageRepository, times(1)).save(mockMessage);
    }
}
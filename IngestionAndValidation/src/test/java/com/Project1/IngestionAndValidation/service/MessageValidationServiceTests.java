package com.Project1.IngestionAndValidation.service;


import com.Project1.IngestionAndValidation.Validation.MessageValidator;
import com.Project1.IngestionAndValidation.Validation.ValidatorRegistry;
import com.Project1.IngestionAndValidation.exception.*;
import com.Project1.IngestionAndValidation.services.AuditService;
import com.Project1.IngestionAndValidation.services.DuplicateCheckService;
import com.Project1.IngestionAndValidation.services.MessageProducerService;
import com.Project1.IngestionAndValidation.services.MessageValidationService;
import com.Project1.IngestionAndValidation.utils.MessageIdGenerator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MessageValidationServiceTest {

    @Mock
    private ValidatorRegistry validatorRegistry;

    @Mock
    private AuditService auditService;

    @Mock
    private DuplicateCheckService duplicateCheckService;

    @Mock
    private MessageIdGenerator messageIdGenerator;

    @Mock
    private MessageProducerService messageProducerService;

    @Mock
    private MessageValidator messageValidator;

    private MessageValidationService messageValidationService;
    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        messageValidationService = new MessageValidationService(
                validatorRegistry, auditService, null, duplicateCheckService,
                messageIdGenerator, messageProducerService
        );
    }



    @Test
    void testProcessIncoming_InvalidJson_ThrowsInvalidMessageException() {
        String payload = "invalid json";

        InvalidMessageException exception = assertThrows(
                InvalidMessageException.class,
                () -> messageValidationService.processIncoming(payload)
        );

        assertTrue(exception.getMessage().contains("Malformed JSON"));
    }

    @Test
    void testProcessIncoming_UnsupportedNetwork_ThrowsUnsupportedNetworkException() {
        String payload = "{\"network\":\"unknown\",\"tenantId\":\"tenant-1\"}";

        when(validatorRegistry.getValidator("unknown"))
                .thenThrow(new UnsupportedNetworkException("Unsupported network: unknown"));

        UnsupportedNetworkException exception = assertThrows(
                UnsupportedNetworkException.class,
                () -> messageValidationService.processIncoming(payload)
        );

        assertTrue(exception.getMessage().contains("Unsupported network: unknown"));
    }

    @Test
    void testProcessIncoming_DuplicateMessage_ReturnsDuplicateMessageString() throws Exception {
        String payload = "{\"network\":\"email\",\"tenantId\":\"tenant-1\"}";
        JsonNode root = objectMapper.readTree(payload);

        when(messageIdGenerator.generate(any(JsonNode.class))).thenReturn("stable-123");
        when(validatorRegistry.getValidator("email")).thenReturn(messageValidator);
        when(duplicateCheckService.isDuplicate("stable-123")).thenReturn(true);

        String result = messageValidationService.processIncoming(payload);

        assertEquals("Duplicate message detected. ID=stable-123", result);
        verify(duplicateCheckService).isDuplicate("stable-123");
        verify(auditService, times(2)).logEvent(anyString(), any(), anyString(), anyString(), any());
    }


    @Test
    void testProcessIncoming_ValidationFails_ThrowsValidationException() throws Exception {
        String payload = "{\"network\":\"email\",\"tenantId\":\"tenant-1\"}";
        JsonNode root = objectMapper.readTree(payload);

        when(messageIdGenerator.generate(any(JsonNode.class))).thenReturn("stable-123");
        when(validatorRegistry.getValidator("email")).thenReturn(messageValidator);
        doThrow(new RuntimeException("Validation failed")).when(messageValidator).validate(anyString());

        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> messageValidationService.processIncoming(payload)
        );

        assertTrue(exception.getMessage().contains("Validation failed for network: email"));
        verify(messageValidator).validate(payload);
    }

    @Test
    void testProcessIncoming_AuditLoggingFails_ThrowsAuditLoggingException() throws Exception {
        String payload = "{\"network\":\"email\",\"tenantId\":\"tenant-1\"}";
        JsonNode root = objectMapper.readTree(payload);

        when(messageIdGenerator.generate(any(JsonNode.class))).thenReturn("stable-123");
        doThrow(new RuntimeException("Audit error")).when(auditService)
                .logEvent(anyString(), any(), anyString(), anyString(), any());

        AuditLoggingException exception = assertThrows(
                AuditLoggingException.class,
                () -> messageValidationService.processIncoming(payload)
        );

        assertTrue(exception.getMessage().contains("Failed to log INGESTED event"));
    }
}
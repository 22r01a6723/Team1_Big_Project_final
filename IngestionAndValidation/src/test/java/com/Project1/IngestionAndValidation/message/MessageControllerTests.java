package com.Project1.IngestionAndValidation.message;


import com.Project1.IngestionAndValidation.Ingestioncontrollers.MessageController;
import com.Project1.IngestionAndValidation.exception.CompanyVaultException;
import com.Project1.IngestionAndValidation.exception.InvalidMessageException;
import com.Project1.IngestionAndValidation.services.MessageValidationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MessageControllerTests {

    @Mock
    private MessageValidationService messageValidationService;

    private MessageController messageController;

    @BeforeEach
    void setUp() {
        messageController = new MessageController(messageValidationService);
    }

    @Test
    void testIngestMessage_Success() {
        String payload = "{\"network\":\"email\",\"tenantId\":\"test-tenant\"}";
        when(messageValidationService.processIncoming(anyString())).thenReturn("Message processed successfully");

        ResponseEntity<String> response = messageController.ingestMessage(payload);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Message processed successfully", response.getBody());
        verify(messageValidationService).processIncoming(payload);
    }

    @Test
    void testIngestMessage_InvalidMessageException() {
        String payload = "invalid json";
        when(messageValidationService.processIncoming(anyString()))
                .thenThrow(new InvalidMessageException("Invalid message format"));

        ResponseEntity<String> response = messageController.ingestMessage(payload);

        assertEquals(400, response.getStatusCodeValue());
        assertTrue(response.getBody().contains("InvalidMessageException"));
        assertTrue(response.getBody().contains("Invalid message format"));
    }

    @Test
    void testIngestMessage_CompanyVaultException() {
        String payload = "{\"network\":\"email\"}";
        when(messageValidationService.processIncoming(anyString()))
                .thenThrow(new CompanyVaultException("Database error"));

        ResponseEntity<String> response = messageController.ingestMessage(payload);

        assertEquals(500, response.getStatusCodeValue());
        assertTrue(response.getBody().contains("CompanyVaultException"));
        assertTrue(response.getBody().contains("Database error"));
    }

    @Test
    void testIngestMessage_GenericException() {
        String payload = "{\"network\":\"email\"}";
        when(messageValidationService.processIncoming(anyString()))
                .thenThrow(new RuntimeException("Unexpected error"));

        ResponseEntity<String> response = messageController.ingestMessage(payload);

        assertEquals(500, response.getStatusCodeValue());
        assertTrue(response.getBody().contains("RuntimeException"));
        assertTrue(response.getBody().contains("Unexpected error"));
    }

    @Test
    void testIngestMessage_DuplicateMessage() {
        String payload = "{\"network\":\"email\",\"tenantId\":\"t1\"}";
        when(messageValidationService.processIncoming(anyString()))
                .thenReturn("Duplicate message detected. ID=12345");

        ResponseEntity<String> response = messageController.ingestMessage(payload);

        assertEquals(200, response.getStatusCodeValue());
        assertTrue(response.getBody().contains("Duplicate message detected"));
    }

    @Test
    void testIngestMessage_EmptyPayload() {
        String payload = "";
        when(messageValidationService.processIncoming(anyString()))
                .thenThrow(new InvalidMessageException("Empty payload"));

        ResponseEntity<String> response = messageController.ingestMessage(payload);

        assertEquals(400, response.getStatusCodeValue());
        assertTrue(response.getBody().contains("Empty payload"));
    }

    @Test
    void testIngestMessage_NullPayload() {
        when(messageValidationService.processIncoming(null))
                .thenThrow(new InvalidMessageException("Null payload"));

        ResponseEntity<String> response = messageController.ingestMessage(null);

        assertEquals(400, response.getStatusCodeValue());
        assertTrue(response.getBody().contains("Null payload"));
    }

    @Test
    void testIngestMessage_WhitespacePayload() {
        String payload = "   ";
        when(messageValidationService.processIncoming(anyString()))
                .thenThrow(new InvalidMessageException("Whitespace payload"));

        ResponseEntity<String> response = messageController.ingestMessage(payload);

        assertEquals(400, response.getStatusCodeValue());
        assertTrue(response.getBody().contains("Whitespace payload"));
    }

    @Test
    void testIngestMessage_SuccessWithDifferentNetwork() {
        String payload = "{\"network\":\"slack\",\"tenantId\":\"t2\"}";
        when(messageValidationService.processIncoming(anyString())).thenReturn("Message processed successfully via Slack");

        ResponseEntity<String> response = messageController.ingestMessage(payload);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Message processed successfully via Slack", response.getBody());
    }

    @Test
    void testIngestMessage_LongPayload() {
        String payload = "{\"network\":\"email\",\"tenantId\":\"" + "a".repeat(1000) + "\"}";
        when(messageValidationService.processIncoming(anyString())).thenReturn("Message processed successfully with long payload");

        ResponseEntity<String> response = messageController.ingestMessage(payload);

        assertEquals(200, response.getStatusCodeValue());
        assertTrue(response.getBody().contains("long payload"));
    }

    @Test
    void testIngestMessage_MultipleInvocations() {
        String payload = "{\"network\":\"email\",\"tenantId\":\"test-tenant\"}";
        when(messageValidationService.processIncoming(anyString())).thenReturn("Processed");

        messageController.ingestMessage(payload);
        messageController.ingestMessage(payload);

        verify(messageValidationService, times(2)).processIncoming(payload);
    }

    @Test
    void testIngestMessage_ExceptionMessageIsReturned() {
        String payload = "{\"network\":\"email\"}";
        when(messageValidationService.processIncoming(anyString()))
                .thenThrow(new RuntimeException("Kafka unavailable"));

        ResponseEntity<String> response = messageController.ingestMessage(payload);

        assertEquals(500, response.getStatusCodeValue());
        assertTrue(response.getBody().contains("Kafka unavailable"));
    }

    @Test
    void testIngestMessage_ResponseEntityNotNull() {
        String payload = "{\"network\":\"email\",\"tenantId\":\"t3\"}";
        when(messageValidationService.processIncoming(anyString())).thenReturn("OK");

        ResponseEntity<String> response = messageController.ingestMessage(payload);

        assertNotNull(response);
        assertEquals("OK", response.getBody());
    }

    @Test
    void testIngestMessage_HandlesTrimmedPayload() {
        String payload = "   {\"network\":\"email\",\"tenantId\":\"t4\"}   ";
        when(messageValidationService.processIncoming(anyString())).thenReturn("Trimmed payload processed");

        ResponseEntity<String> response = messageController.ingestMessage(payload.trim());

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Trimmed payload processed", response.getBody());
    }

}

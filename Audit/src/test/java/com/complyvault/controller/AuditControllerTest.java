package com.complyvault.audit.controller;


import com.complyvault.audit.audit.controller.AuditController;
import com.complyvault.audit.audit.model.AuditEvent;
import com.complyvault.audit.audit.service.AuditService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuditControllerTest {

    @Mock
    private AuditService auditService;

    @InjectMocks
    private AuditController auditController;

    private AuditController.AuditEventRequest auditEventRequest;
    private AuditEvent auditEvent;

    @BeforeEach
    void setUp() {
        auditEventRequest = new AuditController.AuditEventRequest();
        auditEventRequest.setTenantId("bank-001");
        auditEventRequest.setMessageId("msg-123");
        auditEventRequest.setNetwork("email");
        auditEventRequest.setEventType("INGESTED");
        auditEventRequest.setPerformedBy("ingestion-service");
        auditEventRequest.setServiceName("ingestion-service");
        auditEventRequest.setDetails(Map.of("filePath", "/path/to/file"));

        auditEvent = AuditEvent.builder()
                .id("audit-1")
                .tenantId("bank-001")
                .messageId("msg-123")
                .network("email")
                .eventType("INGESTED")
                .timestamp(Instant.now())
                .performedBy("ingestion-service")
                .serviceName("ingestion-service")
                .details(Map.of("filePath", "/path/to/file"))
                .build();
    }

    @Test
    void logEvent_ShouldCallServiceAndReturnOk() {
        // Act
        ResponseEntity<String> response = auditController.logEvent(auditEventRequest);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Audit event logged successfully", response.getBody());
        verify(auditService).logEvent(
                "bank-001", "msg-123", "email", "INGESTED",
                "ingestion-service", "ingestion-service", Map.of("filePath", "/path/to/file")
        );
    }

    @Test
    void getAuditEventsByTenant_ShouldReturnEvents() {
        // Arrange
        List<AuditEvent> events = Arrays.asList(auditEvent);
        when(auditService.getAuditEventsByTenant("bank-001")).thenReturn(events);

        // Act
        ResponseEntity<List<AuditEvent>> response = auditController.getAuditEventsByTenant("bank-001");

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        assertEquals(auditEvent, response.getBody().get(0));
    }

    @Test
    void getAuditEventsByMessageId_ShouldReturnEvents() {
        // Arrange
        List<AuditEvent> events = Arrays.asList(auditEvent);
        when(auditService.getAuditEventsByMessageId("msg-123")).thenReturn(events);

        // Act
        ResponseEntity<List<AuditEvent>> response = auditController.getAuditEventsByMessageId("msg-123");

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        assertEquals(auditEvent, response.getBody().get(0));
    }

    @Test
    void getAuditEventsByEventType_ShouldReturnEvents() {
        // Arrange
        List<AuditEvent> events = Arrays.asList(auditEvent);
        when(auditService.getAuditEventsByEventType("INGESTED")).thenReturn(events);

        // Act
        ResponseEntity<List<AuditEvent>> response = auditController.getAuditEventsByEventType("INGESTED");

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        assertEquals(auditEvent, response.getBody().get(0));
    }

    @Test
    void getAuditEventsByService_ShouldReturnEvents() {
        // Arrange
        List<AuditEvent> events = Arrays.asList(auditEvent);
        when(auditService.getAuditEventsByService("ingestion-service")).thenReturn(events);

        // Act
        ResponseEntity<List<AuditEvent>> response = auditController.getAuditEventsByService("ingestion-service");

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        assertEquals(auditEvent, response.getBody().get(0));
    }

    @Test
    void getAuditEventsByTimeRange_ShouldReturnEvents() {
        // Arrange
        Instant startTime = Instant.now().minusSeconds(3600);
        Instant endTime = Instant.now();
        List<AuditEvent> events = Arrays.asList(auditEvent);
        when(auditService.getAuditEventsByTimeRange(startTime, endTime)).thenReturn(events);

        // Act
        ResponseEntity<List<AuditEvent>> response = auditController.getAuditEventsByTimeRange(startTime, endTime);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
    }

    @Test
    void getAuditEventsByTenantAndTimeRange_ShouldReturnEvents() {
        // Arrange
        Instant startTime = Instant.now().minusSeconds(3600);
        Instant endTime = Instant.now();
        List<AuditEvent> events = Arrays.asList(auditEvent);
        when(auditService.getAuditEventsByTenantAndTimeRange("bank-001", startTime, endTime)).thenReturn(events);

        // Act
        ResponseEntity<List<AuditEvent>> response = auditController.getAuditEventsByTenantAndTimeRange("bank-001", startTime, endTime);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
    }

    @Test
    void searchAuditEvents_ShouldReturnEvents() {
        // Arrange
        Instant startTime = Instant.now().minusSeconds(3600);
        Instant endTime = Instant.now();
        List<AuditEvent> events = Arrays.asList(auditEvent);
        when(auditService.searchAuditEvents("bank-001", "INGESTED", startTime, endTime)).thenReturn(events);

        // Act
        ResponseEntity<List<AuditEvent>> response = auditController.searchAuditEvents("bank-001", "INGESTED", startTime, endTime);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
    }

    @Test
    void checkDuplicate_ShouldReturnDuplicateStatus() {
        // Arrange
        when(auditService.isDuplicate("msg-123")).thenReturn(true);

        // Act
        ResponseEntity<Map<String, Boolean>> response = auditController.checkDuplicate("msg-123");

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().get("isDuplicate"));
    }

    @Test
    void auditEventRequest_GettersAndSetters_ShouldWork() {
        // Arrange
        AuditController.AuditEventRequest request = new AuditController.AuditEventRequest();

        // Act
        request.setTenantId("test-tenant");
        request.setMessageId("test-message");
        request.setNetwork("test-network");
        request.setEventType("test-event");
        request.setPerformedBy("test-user");
        request.setServiceName("test-service");
        request.setDetails(Map.of("key", "value"));

        // Assert
        assertEquals("test-tenant", request.getTenantId());
        assertEquals("test-message", request.getMessageId());
        assertEquals("test-network", request.getNetwork());
        assertEquals("test-event", request.getEventType());
        assertEquals("test-user", request.getPerformedBy());
        assertEquals("test-service", request.getServiceName());
        assertEquals("value", request.getDetails().get("key"));
    }
}

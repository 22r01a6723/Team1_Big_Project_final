package com.complyvault.service;


import com.complyvault.audit.model.AuditEvent;
import com.complyvault.audit.repository.AuditEventRepository;
import com.complyvault.audit.service.AuditService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuditServiceTest {

    @Mock
    private AuditEventRepository auditEventRepository;

    @InjectMocks
    private AuditService auditService;

    private AuditEvent sampleEvent;

    @BeforeEach
    void setUp() {
        sampleEvent = AuditEvent.builder()
                .id("audit-1")
                .tenantId("bank-001")
                .messageId("msg-123")
                .network("email")
                .eventType("INGESTED")
                .timestamp(Instant.now())
                .performedBy("tester")
                .serviceName("audit-service")
                .details(Map.of("field", "value"))
                .build();
    }

    @Test
    void logEvent_ShouldSaveEvent() {
        // Act
        auditService.logEvent("bank-001", "msg-123", "email", "INGESTED", "tester", "audit-service", Map.of("field", "value"));

        // Assert
        verify(auditEventRepository).save(any(AuditEvent.class));
    }

    @Test
    void logEvent_WithOverloadedMethod_ShouldDelegate() {
        // Act
        auditService.logEvent("bank-001", "msg-123", "email", "INGESTED", "audit-service", Map.of("field", "value"));

        // Assert
        verify(auditEventRepository).save(any(AuditEvent.class));
    }

    @Test
    void getAuditEventsByTenant_ShouldReturnEvents() {
        when(auditEventRepository.findByTenantId("bank-001")).thenReturn(List.of(sampleEvent));

        List<AuditEvent> result = auditService.getAuditEventsByTenant("bank-001");

        assertEquals(1, result.size());
        assertEquals("msg-123", result.get(0).getMessageId());
    }

    @Test
    void getAuditEventsByMessageId_ShouldReturnEvents() {
        when(auditEventRepository.findByMessageId("msg-123")).thenReturn(List.of(sampleEvent));

        List<AuditEvent> result = auditService.getAuditEventsByMessageId("msg-123");

        assertEquals(1, result.size());
        assertEquals("bank-001", result.get(0).getTenantId());
    }

    @Test
    void getAuditEventsByEventType_ShouldReturnEvents() {
        when(auditEventRepository.findByEventType("INGESTED")).thenReturn(List.of(sampleEvent));

        List<AuditEvent> result = auditService.getAuditEventsByEventType("INGESTED");

        assertFalse(result.isEmpty());
        assertEquals("INGESTED", result.get(0).getEventType());
    }

    @Test
    void getAuditEventsByService_ShouldReturnEvents() {
        when(auditEventRepository.findByServiceName("audit-service")).thenReturn(List.of(sampleEvent));

        List<AuditEvent> result = auditService.getAuditEventsByService("audit-service");

        assertEquals(1, result.size());
        assertEquals("audit-service", result.get(0).getServiceName());
    }

    @Test
    void getAuditEventsByTimeRange_ShouldReturnEvents() {
        Instant start = Instant.now().minusSeconds(60);
        Instant end = Instant.now().plusSeconds(60);

        when(auditEventRepository.findByTimestampBetween(start, end)).thenReturn(List.of(sampleEvent));

        List<AuditEvent> result = auditService.getAuditEventsByTimeRange(start, end);

        assertEquals(1, result.size());
    }

    @Test
    void getAuditEventsByTenantAndTimeRange_ShouldReturnEvents() {
        Instant start = Instant.now().minusSeconds(60);
        Instant end = Instant.now().plusSeconds(60);

        when(auditEventRepository.findByTenantIdAndTimestampBetween("bank-001", start, end)).thenReturn(List.of(sampleEvent));

        List<AuditEvent> result = auditService.getAuditEventsByTenantAndTimeRange("bank-001", start, end);

        assertEquals(1, result.size());
    }

    @Test
    void isDuplicate_ShouldReturnTrue_WhenEventExists() {
        when(auditEventRepository.existsByMessageId("msg-123")).thenReturn(true);

        assertTrue(auditService.isDuplicate("msg-123"));
    }

    @Test
    void searchAuditEvents_ShouldReturnEvents() {
        Instant start = Instant.now().minusSeconds(60);
        Instant end = Instant.now().plusSeconds(60);

        when(auditEventRepository.findAuditEventsByTenantAndTypeAndTimeRange("bank-001", "INGESTED", start, end))
                .thenReturn(List.of(sampleEvent));

        List<AuditEvent> result = auditService.searchAuditEvents("bank-001", "INGESTED", start, end);

        assertEquals(1, result.size());
        assertEquals("msg-123", result.get(0).getMessageId());
    }
}


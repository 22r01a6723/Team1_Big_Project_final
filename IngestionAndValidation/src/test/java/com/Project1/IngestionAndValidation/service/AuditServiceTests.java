package com.Project1.IngestionAndValidation.service;


import com.Project1.IngestionAndValidation.Models.AuditEvent;
import com.Project1.IngestionAndValidation.repository.AuditEventRepository;
import com.Project1.IngestionAndValidation.services.AuditService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuditServiceTests {

    @Mock
    private AuditEventRepository auditEventRepository;

    private AuditService auditService;

    @BeforeEach
    void setUp() {
        auditService = new AuditService(auditEventRepository);
    }

    @Test
    void testLogEvent() {
        auditService.logEvent("tenant-1", "msg-1", "email", "TEST_EVENT", Map.of("key", "value"));

        verify(auditEventRepository).save(any(AuditEvent.class));
    }

    @Test
    void testLogEventWithNullValues() {
        auditService.logEvent(null, null, null, "TEST_EVENT", Map.of());

        verify(auditEventRepository).save(any(AuditEvent.class));
    }

    @Test
    void testLogEventWithEmptyDetails() {
        auditService.logEvent("tenant-1", "msg-1", "email", "TEST_EVENT", Map.of());

        verify(auditEventRepository).save(any(AuditEvent.class));
    }

    @Test
    void testIsDuplicate() {
        when(auditEventRepository.existsByMessageId("msg-1")).thenReturn(true);

        boolean result = auditService.isDuplicate("msg-1");

        assertTrue(result);
        verify(auditEventRepository).existsByMessageId("msg-1");
    }

    @Test
    void testIsNotDuplicate() {
        when(auditEventRepository.existsByMessageId("msg-1")).thenReturn(false);

        boolean result = auditService.isDuplicate("msg-1");

        assertFalse(result);
        verify(auditEventRepository).existsByMessageId("msg-1");
    }

    @Test
    void testLogEventCreatesValidAuditEvent() {
        ArgumentCaptor<AuditEvent> captor = ArgumentCaptor.forClass(AuditEvent.class);

        auditService.logEvent("tenant-1", "msg-1", "email", "TEST_EVENT", Map.of("detail", "value"));

        verify(auditEventRepository).save(captor.capture());
        AuditEvent savedEvent = captor.getValue();

        assertNotNull(savedEvent.getAuditId());
        assertEquals("msg-1", savedEvent.getMessageId());
        assertEquals("tenant-1", savedEvent.getTenantId());
        assertEquals("email", savedEvent.getNetwork());
        assertEquals("TEST_EVENT", savedEvent.getEventType());
        assertNotNull(savedEvent.getTimestamp());
        assertEquals("IngestionAndValidationApp", savedEvent.getPerformedBy());
        assertEquals("value", savedEvent.getDetails().get("detail"));
    }

    @Test
    void testLogEventWithNullDetailsMap() {
        auditService.logEvent("tenant-1", "msg-1", "email", "TEST_EVENT", null);
        verify(auditEventRepository).save(any(AuditEvent.class));
    }


    @Test
    void testIsDuplicateWithNullMessageId() {
        when(auditEventRepository.existsByMessageId(null)).thenReturn(false);
        assertFalse(auditService.isDuplicate(null));
        verify(auditEventRepository).existsByMessageId(null);
    }

    @Test
    void testLogEventRepositoryThrowsException() {
        doThrow(new RuntimeException("DB error")).when(auditEventRepository).save(any(AuditEvent.class));

        assertThrows(RuntimeException.class, () ->
                auditService.logEvent("tenant-1", "msg-3", "teams", "FAIL_EVENT", Map.of("k", "v"))
        );
    }

    @Test
    void testLogEventWithEmptyStrings() {
        auditService.logEvent("", "", "", "EMPTY_EVENT", Collections.emptyMap());
        verify(auditEventRepository).save(any(AuditEvent.class));
    }

    @Test
    void testMultipleLogEvents() {
        auditService.logEvent("tenant-1", "msg-10", "email", "E1", Map.of("k1", "v1"));
        auditService.logEvent("tenant-1", "msg-11", "slack", "E2", Map.of("k2", "v2"));

        verify(auditEventRepository, times(2)).save(any(AuditEvent.class));
    }
}
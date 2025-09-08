package com.project_1.normalizer.service;


import com.project_1.normalizer.model.AuditEvent;
import com.project_1.normalizer.repository.AuditRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuditServiceTest {

    @Mock
    private AuditRepository auditRepository;

    private AuditService auditService;

    @BeforeEach
    void setUp() {
        auditService = new AuditService(auditRepository);
    }

    @Test
    void testLogEvent() {
        auditService.logEvent("tenant-1", "msg-1", "email", "TEST_EVENT", Map.of("key", "value"));

        verify(auditRepository).save(any(AuditEvent.class));
    }

    @Test
    void testLogEventWithNullValues() {
        auditService.logEvent(null, null, null, "TEST_EVENT", Map.of());

        verify(auditRepository).save(any(AuditEvent.class));
    }

    @Test
    void testLogEventWithEmptyDetails() {
        auditService.logEvent("tenant-1", "msg-1", "email", "TEST_EVENT", Map.of());

        verify(auditRepository).save(any(AuditEvent.class));
    }

    @Test
    void testLogEventCreatesValidAuditEvent() {
        ArgumentCaptor<AuditEvent> captor = ArgumentCaptor.forClass(AuditEvent.class);

        auditService.logEvent("tenant-1", "msg-1", "email", "TEST_EVENT", Map.of("detail", "value"));

        verify(auditRepository).save(captor.capture());
        AuditEvent savedEvent = captor.getValue();

        assertNotNull(savedEvent.getId());
        assertEquals("msg-1", savedEvent.getMessageId());
        assertEquals("tenant-1", savedEvent.getTenantId());
        assertEquals("email", savedEvent.getNetwork());
        assertEquals("TEST_EVENT", savedEvent.getEventType());
        assertNotNull(savedEvent.getTimestamp());
        assertEquals("NormalizerApp", savedEvent.getPerformedBy());
        assertEquals("value", savedEvent.getDetails().get("detail"));
    }
    @Test
    void testLogEventWithMultipleDetails() {
        ArgumentCaptor<AuditEvent> captor = ArgumentCaptor.forClass(AuditEvent.class);

        auditService.logEvent("tenant-2", "msg-2", "sms", "MULTI_DETAIL_EVENT",
                Map.of("detail1", "value1", "detail2", "value2"));

        verify(auditRepository).save(captor.capture());
        AuditEvent savedEvent = captor.getValue();

        assertEquals(2, savedEvent.getDetails().size());
        assertEquals("value1", savedEvent.getDetails().get("detail1"));
        assertEquals("value2", savedEvent.getDetails().get("detail2"));
    }

    @Test
    void testLogEventWithDifferentEventType() {
        ArgumentCaptor<AuditEvent> captor = ArgumentCaptor.forClass(AuditEvent.class);

        auditService.logEvent("tenant-3", "msg-3", "chat", "LOGIN_EVENT", Map.of());

        verify(auditRepository).save(captor.capture());
        AuditEvent savedEvent = captor.getValue();

        assertEquals("LOGIN_EVENT", savedEvent.getEventType());
        assertEquals("chat", savedEvent.getNetwork());
    }

    @Test
    void testLogEventGeneratesUniqueIds() {
        ArgumentCaptor<AuditEvent> captor = ArgumentCaptor.forClass(AuditEvent.class);

        auditService.logEvent("tenant-4", "msg-4", "api", "UNIQUE_TEST", Map.of());
        auditService.logEvent("tenant-4", "msg-5", "api", "UNIQUE_TEST", Map.of());

        verify(auditRepository, times(2)).save(captor.capture());

        AuditEvent first = captor.getAllValues().get(0);
        AuditEvent second = captor.getAllValues().get(1);

        assertNotEquals(first.getId(), second.getId(), "Each event should have a unique ID");
    }

    @Test
    void testLogEventSetsTimestamp() {
        ArgumentCaptor<AuditEvent> captor = ArgumentCaptor.forClass(AuditEvent.class);

        auditService.logEvent("tenant-6", "msg-6", "system", "TIMESTAMP_EVENT", Map.of());

        verify(auditRepository).save(captor.capture());
        AuditEvent savedEvent = captor.getValue();

        assertNotNull(savedEvent.getTimestamp(), "Timestamp should not be null");
    }


    @Test
    void testLogEventWhenRepositoryThrowsException() {
        doThrow(new RuntimeException("DB error")).when(auditRepository).save(any(AuditEvent.class));

        assertThrows(RuntimeException.class, () ->
                auditService.logEvent("tenant-8", "msg-8", "web", "ERROR_EVENT", Map.of("k", "v"))
        );

        verify(auditRepository).save(any(AuditEvent.class));
    }

}
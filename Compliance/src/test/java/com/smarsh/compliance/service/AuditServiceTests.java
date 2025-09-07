package com.smarsh.compliance.service;

import com.smarsh.compliance.mongodb.AuditRepository;
import com.smarsh.compliance.models.AuditEvent;
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
    void testLogEvent_CallsRepositorySave() {
        auditService.logEvent("tenant-1", "msg-1", "email", "TEST_EVENT", Map.of("key", "value"));

        verify(auditRepository).save(any(AuditEvent.class));
    }

    @Test
    void testLogEvent_NullValues_StillSaves() {
        auditService.logEvent(null, null, null, "TEST_EVENT", Map.of());

        verify(auditRepository).save(any(AuditEvent.class));
    }

    @Test
    void testLogEvent_EmptyDetails_StillSaves() {
        auditService.logEvent("tenant-1", "msg-1", "email", "TEST_EVENT", Map.of());

        verify(auditRepository).save(any(AuditEvent.class));
    }

    @Test
    void testLogEvent_CorrectEventStructure() {
        ArgumentCaptor<AuditEvent> captor = ArgumentCaptor.forClass(AuditEvent.class);

        auditService.logEvent("tenant-1", "msg-1", "email", "MESSAGE_RECEIVED", Map.of("detail", "value"));

        verify(auditRepository).save(captor.capture());
        AuditEvent savedEvent = captor.getValue();

        assertEquals("tenant-1", savedEvent.getTenantId());
        assertEquals("msg-1", savedEvent.getMessageId());
        assertEquals("email", savedEvent.getNetwork());
        assertEquals("MESSAGE_RECEIVED", savedEvent.getEventType());
        assertEquals("Compliance-App", savedEvent.getPerformedBy());
        assertNotNull(savedEvent.getId());
        assertNotNull(savedEvent.getTimestamp());
    }

    // ---------- Edge Cases ----------

    @Test
    void testLogEvent_NullEventType_StillSaves() {
        auditService.logEvent("tenant-2", "msg-2", "chat", null, Map.of());

        verify(auditRepository).save(any(AuditEvent.class));
    }

    @Test
    void testLogEvent_SpecialCharactersInFields() {
        ArgumentCaptor<AuditEvent> captor = ArgumentCaptor.forClass(AuditEvent.class);

        auditService.logEvent("tenant-!@#", "msg-©", "sms$", "LOGIN#EVENT", Map.of("weirdKey*&", "val%%"));

        verify(auditRepository).save(captor.capture());
        AuditEvent event = captor.getValue();

        assertEquals("tenant-!@#", event.getTenantId());
        assertEquals("msg-©", event.getMessageId());
        assertEquals("sms$", event.getNetwork());
        assertEquals("LOGIN#EVENT", event.getEventType());
    }

    @Test
    void testLogEvent_NullDetailsMap_StillSaves() {
        assertDoesNotThrow(() -> auditService.logEvent("tenant-3", "msg-3", "email", "NULL_MAP", null));
        verify(auditRepository).save(any(AuditEvent.class));
    }

    @Test
    void testLogEvent_LongTenantId_SavesCorrectly() {
        String longTenantId = "tenant-" + "X".repeat(500);

        auditService.logEvent(longTenantId, "msg-4", "email", "LONG_TENANT", Map.of());

        verify(auditRepository).save(any(AuditEvent.class));
    }

    @Test
    void testLogEvent_NullNetworkField_SavesCorrectly() {
        auditService.logEvent("tenant-5", "msg-5", null, "NO_NETWORK", Map.of("action", "test"));

        verify(auditRepository).save(any(AuditEvent.class));
    }

    @Test
    void testLogEvent_MultipleSequentialCalls_AllSaved() {
        auditService.logEvent("tenant-7", "msg-7", "email", "EVENT1", Map.of());
        auditService.logEvent("tenant-8", "msg-8", "chat", "EVENT2", Map.of("k", "v"));

        verify(auditRepository, times(2)).save(any(AuditEvent.class));
    }
}

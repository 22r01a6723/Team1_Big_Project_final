package com.project_1.normalizer.service;


import com.project_1.normalizer.model.CanonicalMessage;
import com.project_1.normalizer.repository.MessageRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MongoStorageServiceTest {

    @Mock
    private MessageRepository messageRepository;

    @Mock
    private AuditService auditService;

    private MongoStorageService mongoStorageService;

    @BeforeEach
    void setUp() {
        mongoStorageService = new MongoStorageService(messageRepository, auditService);
    }

    @Test
    void testStore_WithNullRawJson_StillStoresMessage() {
        CanonicalMessage message = CanonicalMessage.builder()
                .messageId("msg-2")
                .tenantId("tenant-2")
                .network("sms")
                .build();

        mongoStorageService.store(message, null);

        verify(messageRepository).save(message);
        verify(auditService).logEvent(
                eq("tenant-2"), eq("msg-2"), eq("sms"),
                eq("STORED_MONGODB"), any(Map.class)
        );
    }


    @Test
    void testStore_MessageMissingTenantOrNetwork_StillLogsEvent() {
        CanonicalMessage message = CanonicalMessage.builder()
                .messageId("msg-5")
                .build(); // no tenantId, no network

        mongoStorageService.store(message, "raw data");

        verify(messageRepository).save(message);
        verify(auditService).logEvent(
                isNull(), eq("msg-5"), isNull(),
                eq("STORED_MONGODB"), any(Map.class)
        );
    }


    @Test
    void testStore_Success() {
        CanonicalMessage message = CanonicalMessage.builder()
                .messageId("msg-1")
                .tenantId("tenant-1")
                .network("email")
                .build();

        mongoStorageService.store(message, "raw json");

        verify(messageRepository).save(message);
        verify(auditService).logEvent(
                eq("tenant-1"), eq("msg-1"), eq("email"),
                eq("STORED_MONGODB"), any(Map.class)
        );
    }

    @Test
    void testStore_RepositoryThrowsException_LogsError() {
        CanonicalMessage message = CanonicalMessage.builder()
                .messageId("msg-1")
                .tenantId("tenant-1")
                .network("email")
                .build();

        doThrow(new RuntimeException("DB error")).when(messageRepository).save(any());

        assertDoesNotThrow(() -> mongoStorageService.store(message, "raw json"));

        verify(messageRepository).save(message);
        verify(auditService).logEvent(
                eq("tenant-1"), eq("msg-1"), eq("email"),
                eq("STORE_MONGODB_FAILED"), any(Map.class)
        );
    }

    @Test
    void testIsDuplicate_MessageExists_ReturnsTrue() {
        when(messageRepository.existsById("msg-1")).thenReturn(true);

        boolean result = mongoStorageService.isDuplicate("msg-1");

        assertTrue(result);
        verify(messageRepository).existsById("msg-1");
    }

    @Test
    void testIsDuplicate_MessageNotExists_ReturnsFalse() {
        when(messageRepository.existsById("msg-1")).thenReturn(false);

        boolean result = mongoStorageService.isDuplicate("msg-1");

        assertFalse(result);
        verify(messageRepository).existsById("msg-1");
    }

    @Test
    void testIsDuplicate_NullId_ReturnsFalse() {
        boolean result = mongoStorageService.isDuplicate(null);

        assertFalse(result);
        verify(messageRepository, never()).existsById(anyString());
    }
}
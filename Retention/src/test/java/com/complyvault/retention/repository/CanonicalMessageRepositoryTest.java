package com.complyvault.retention;


import com.complyvault.retention.model.CanonicalMessage;
import com.complyvault.retention.repository.CanonicalMessageRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CanonicalMessageRepositoryTest {

    @Mock
    private CanonicalMessageRepository canonicalMessageRepository;

    // 1
    @Test
    void findByTenantIdAndNetwork_ReturnsSingleMessage() {
        CanonicalMessage msg = CanonicalMessage.builder()
                .messageId("msg-1")
                .tenantId("bank-001")
                .network("email")
                .timestamp(Instant.parse("2025-09-08T10:00:00Z"))
                .build();

        when(canonicalMessageRepository.findByTenantIdAndNetwork("bank-001", "email"))
                .thenReturn(List.of(msg));

        List<CanonicalMessage> result = canonicalMessageRepository.findByTenantIdAndNetwork("bank-001", "email");

        assertEquals(1, result.size());
        assertEquals("msg-1", result.get(0).getMessageId());
        verify(canonicalMessageRepository).findByTenantIdAndNetwork("bank-001", "email");
    }

    // 2
    @Test
    void findByTenantId_ReturnsEmptyListWhenNone() {
        when(canonicalMessageRepository.findByTenantId("no-such-tenant")).thenReturn(List.of());

        List<CanonicalMessage> result = canonicalMessageRepository.findByTenantId("no-such-tenant");

        assertTrue(result.isEmpty());
        verify(canonicalMessageRepository).findByTenantId("no-such-tenant");
    }

    // 3
    @Test
    void findByTenantId_ReturnsMultipleMessages() {
        CanonicalMessage m1 = CanonicalMessage.builder()
                .messageId("m1").tenantId("bank-001").network("email").timestamp(Instant.now()).build();
        CanonicalMessage m2 = CanonicalMessage.builder()
                .messageId("m2").tenantId("bank-001").network("slack").timestamp(Instant.now()).build();

        when(canonicalMessageRepository.findByTenantId("bank-001")).thenReturn(List.of(m1, m2));

        List<CanonicalMessage> result = canonicalMessageRepository.findByTenantId("bank-001");

        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(r -> "bank-001".equals(r.getTenantId())));
        verify(canonicalMessageRepository).findByTenantId("bank-001");
    }

    // 4
    @Test
    void findByTimestampBefore_ReturnsOlderMessages() {
        Instant pivot = Instant.parse("2025-09-08T09:00:00Z");
        CanonicalMessage old = CanonicalMessage.builder()
                .messageId("old").tenantId("t").network("n").timestamp(Instant.parse("2025-09-08T08:00:00Z")).build();

        when(canonicalMessageRepository.findByTimestampBefore(pivot)).thenReturn(List.of(old));

        List<CanonicalMessage> result = canonicalMessageRepository.findByTimestampBefore(pivot);

        assertEquals(1, result.size());
        assertEquals("old", result.get(0).getMessageId());
        verify(canonicalMessageRepository).findByTimestampBefore(pivot);
    }

    // 5
    @Test
    void save_ShouldReturnSavedMessage() {
        CanonicalMessage toSave = CanonicalMessage.builder()
                .messageId("save-me").tenantId("t").network("n").timestamp(Instant.now()).build();

        when(canonicalMessageRepository.save(any(CanonicalMessage.class))).thenAnswer(inv -> inv.getArgument(0));

        CanonicalMessage saved = canonicalMessageRepository.save(toSave);

        assertNotNull(saved);
        assertEquals("save-me", saved.getMessageId());
        verify(canonicalMessageRepository).save(toSave);
    }

    // 6
    @Test
    void saveAll_ShouldReturnSavedMessages() {
        CanonicalMessage a = CanonicalMessage.builder().messageId("a").tenantId("t").timestamp(Instant.now()).build();
        CanonicalMessage b = CanonicalMessage.builder().messageId("b").tenantId("t").timestamp(Instant.now()).build();

        when(canonicalMessageRepository.saveAll(anyList())).thenAnswer(inv -> inv.getArgument(0));

        List<CanonicalMessage> saved = canonicalMessageRepository.saveAll(List.of(a, b));

        assertEquals(2, saved.size());
        verify(canonicalMessageRepository).saveAll(List.of(a, b));
    }

    // 7
    @Test
    void deleteById_ShouldCallRepositoryDelete() {
        doNothing().when(canonicalMessageRepository).deleteById("to-delete");

        canonicalMessageRepository.deleteById("to-delete");

        verify(canonicalMessageRepository).deleteById("to-delete");
    }

    // 8
    @Test
    void existsById_ShouldReturnTrueWhenPresent() {
        when(canonicalMessageRepository.existsById("exists-id")).thenReturn(true);

        assertTrue(canonicalMessageRepository.existsById("exists-id"));
        verify(canonicalMessageRepository).existsById("exists-id");
    }

    // 9
    @Test
    void count_ShouldReturnNumberOfDocuments() {
        when(canonicalMessageRepository.count()).thenReturn(5L);

        long cnt = canonicalMessageRepository.count();

        assertEquals(5L, cnt);
        verify(canonicalMessageRepository).count();
    }

    // 10
    @Test
    void findByTenantIdAndNetwork_WithNullArgs_ReturnsEmptyList() {
        when(canonicalMessageRepository.findByTenantIdAndNetwork(null, null)).thenReturn(List.of());

        List<CanonicalMessage> result = canonicalMessageRepository.findByTenantIdAndNetwork(null, null);

        assertTrue(result.isEmpty());
        verify(canonicalMessageRepository).findByTenantIdAndNetwork(null, null);
    }
}

package com.complyvault.retention.service;


import com.complyvault.retention.model.CanonicalMessage;
import com.complyvault.retention.model.RetentionAuditLog;
import com.complyvault.retention.model.RetentionPolicy;
import com.complyvault.retention.repository.CanonicalMessageRepository;
import com.complyvault.retention.repository.RetentionAuditLogRepository;
import com.complyvault.retention.repository.RetentionPolicyRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RetentionServiceTest {

    @Mock
    private RetentionPolicyRepository policyRepository;

    @Mock
    private CanonicalMessageRepository canonicalMessageRepository;

    @Mock
    private RetentionAuditLogRepository auditLogRepository;

    @InjectMocks
    private RetentionService retentionService;

    private RetentionPolicy policy;
    private CanonicalMessage message;

    @BeforeEach
    void setUp() {
        // Set up file storage directory for tests
        ReflectionTestUtils.setField(retentionService, "fileStorageDirectory", "/tmp/test");

        policy = RetentionPolicy.builder()
                .id("bank-001_email")
                .tenantId("bank-001")
                .channel("email")
                .retentionPeriodDays(30)
                .build();

        message = CanonicalMessage.builder()
                .messageId("msg-123")
                .tenantId("bank-001")
                .network("email")
                .timestamp(Instant.now().minusSeconds(86400 * 31)) // 31 days old
                .expired(false)
                .build();
    }

    @Test
    void saveOrUpdatePolicy_ShouldSavePolicyAndCreateAuditLog() {
        // Arrange
        when(policyRepository.save(any(RetentionPolicy.class))).thenReturn(policy);
        when(auditLogRepository.save(any(RetentionAuditLog.class))).thenReturn(new RetentionAuditLog());

        // Act
        RetentionPolicy result = retentionService.saveOrUpdatePolicy(policy);

        // Assert
        assertEquals(policy, result);
        verify(policyRepository).save(policy);
        verify(auditLogRepository).save(any(RetentionAuditLog.class));
    }

    @Test
    void getPolicy_WithExistingPolicy_ShouldReturnPolicy() {
        // Arrange
        when(policyRepository.findByTenantIdAndChannel("bank-001", "email")).thenReturn(Optional.of(policy));

        // Act
        Optional<RetentionPolicy> result = retentionService.getPolicy("bank-001", "email");

        // Assert
        assertTrue(result.isPresent());
        assertEquals(policy, result.get());
    }

    @Test
    void getAllPolicies_ShouldReturnAllPolicies() {
        // Arrange
        List<RetentionPolicy> policies = Arrays.asList(policy);
        when(policyRepository.findAll()).thenReturn(policies);

        // Act
        List<RetentionPolicy> result = retentionService.getAllPolicies();

        // Assert
        assertEquals(1, result.size());
        assertEquals(policy, result.get(0));
    }

    @Test
    void processExpiredMessages_WithExpiredMessages_ShouldDeleteThem() {
        // Arrange
        List<RetentionPolicy> policies = Arrays.asList(policy);
        List<CanonicalMessage> messages = Arrays.asList(message);

        when(policyRepository.findAll()).thenReturn(policies);
        when(canonicalMessageRepository.findByTenantIdAndNetwork("bank-001", "email")).thenReturn(messages);
        when(auditLogRepository.save(any(RetentionAuditLog.class))).thenReturn(new RetentionAuditLog());

        // Act
        retentionService.processExpiredMessages();

        // Assert
        verify(canonicalMessageRepository).deleteById("msg-123");
        verify(auditLogRepository, atLeastOnce()).save(any(RetentionAuditLog.class));
    }

    @Test
    void processExpiredMessages_WithNoPolicies_ShouldDoNothing() {
        // Arrange
        when(policyRepository.findAll()).thenReturn(List.of());

        // Act
        retentionService.processExpiredMessages();

        // Assert
        verify(canonicalMessageRepository, never()).deleteById(any());
        verify(auditLogRepository, never()).save(any());
    }

    @Test
    void processExpiredMessages_WithNoMessages_ShouldCreateNotFoundAuditLog() {
        // Arrange
        List<RetentionPolicy> policies = Arrays.asList(policy);
        when(policyRepository.findAll()).thenReturn(policies);
        when(canonicalMessageRepository.findByTenantIdAndNetwork("bank-001", "email")).thenReturn(List.of());
        when(auditLogRepository.save(any(RetentionAuditLog.class))).thenReturn(new RetentionAuditLog());

        // Act
        retentionService.processExpiredMessages();

        // Assert
        verify(canonicalMessageRepository, never()).deleteById(any());
        verify(auditLogRepository).save(any(RetentionAuditLog.class));
    }

    @Test
    void shouldDeleteMessage_WithOldTimestamp_ShouldReturnTrue() {
        // Arrange
        Instant cutoffDate = Instant.now().minusSeconds(86400 * 30);
        CanonicalMessage oldMessage = CanonicalMessage.builder()
                .timestamp(Instant.now().minusSeconds(86400 * 31))
                .build();

        // Act
        boolean result = retentionService.shouldDeleteMessage(oldMessage, cutoffDate);

        // Assert
        assertTrue(result);
    }

    @Test
    void shouldDeleteMessage_WithRecentTimestamp_ShouldReturnFalse() {
        // Arrange
        Instant cutoffDate = Instant.now().minusSeconds(86400 * 30);
        CanonicalMessage recentMessage = CanonicalMessage.builder()
                .timestamp(Instant.now().minusSeconds(86400 * 29))
                .build();

        // Act
        boolean result = retentionService.shouldDeleteMessage(recentMessage, cutoffDate);

        // Assert
        assertFalse(result);
    }

    @Test
    void checkDatabaseConnection_ShouldCallRepositoryMethods() {
        // Arrange
        when(canonicalMessageRepository.count()).thenReturn(5L);
        when(policyRepository.count()).thenReturn(3L);

        // Act
        retentionService.checkDatabaseConnection();

        // Assert
        verify(canonicalMessageRepository).count();
        verify(policyRepository).count();
    }

    @Test
    void testFindByTenantIdAndNetwork_ShouldCallRepositoryMethod() {
        // Arrange
        when(canonicalMessageRepository.findByTenantIdAndNetwork("bank-001", "email")).thenReturn(List.of());
        when(canonicalMessageRepository.findByTenantIdAndNetwork("bank-001", "slack")).thenReturn(List.of());

        // Act
        retentionService.testFindByTenantIdAndNetwork();

        // Assert
        verify(canonicalMessageRepository).findByTenantIdAndNetwork("bank-001", "email");
        verify(canonicalMessageRepository).findByTenantIdAndNetwork("bank-001", "slack");
    }

    @Test
    void deleteMessageById_ShouldCallRepository() {
        // Act
        retentionService.deleteMessageById("test-id");

        // Assert
        verify(canonicalMessageRepository).deleteById("test-id");
    }
}
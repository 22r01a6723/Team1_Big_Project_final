package com.complyvault.retention.service;

import com.complyvault.retention.exception.RetentionNotFoundException;
import com.complyvault.retention.exception.RetentionDataAccessException;
import com.complyvault.retention.exception.RetentionServiceException;
import com.complyvault.retention.repository.CanonicalMessageRepository;
import com.complyvault.retention.repository.RetentionAuditLogRepository;
import com.complyvault.retention.repository.RetentionPolicyRepository;
import com.complyvault.retention.model.RetentionAuditLog;
import com.complyvault.retention.model.RetentionPolicy;
import com.complyvault.retention.model.CanonicalMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class RetentionService {

    private final RetentionPolicyRepository policyRepository;
    private final CanonicalMessageRepository canonicalMessageRepository;
    private final RetentionAuditLogRepository auditLogRepository;

    @Value("${app.disk.path}")
    private String fileStorageDirectory;

    public RetentionService(RetentionPolicyRepository policyRepository,
                            CanonicalMessageRepository canonicalMessageRepository,
                            RetentionAuditLogRepository auditLogRepository) {
        this.policyRepository = policyRepository;
        this.canonicalMessageRepository = canonicalMessageRepository;
        this.auditLogRepository = auditLogRepository;
    }

    public RetentionPolicy saveOrUpdatePolicy(RetentionPolicy policy) {
        String policyId = policy.getTenantId() + "_" + policy.getChannel();
        policy.setId(policyId);
        try {
            RetentionPolicy savedPolicy = policyRepository.save(policy);
            RetentionAuditLog logEntry = RetentionAuditLog.builder()
                    .tenantId(savedPolicy.getTenantId())
                    .channel(savedPolicy.getChannel())
                    .messageId(null)
                    .processedAt(Instant.now())
                    .cutoffDate(null)
                    .retentionDays(savedPolicy.getRetentionPeriodDays())
                    .status("POLICY_CREATED_OR_UPDATED")
                    .details("Retention policy created or updated for tenant "
                            + savedPolicy.getTenantId() + " and channel "
                            + savedPolicy.getChannel())
                    .build();
            auditLogRepository.save(logEntry);
            return savedPolicy;
        } catch (Exception e) {
            log.error("Error saving or updating retention policy: {}", e.getMessage(), e);
            throw new RetentionDataAccessException("Failed to save or update retention policy", e);
        }
    }

    public Optional<RetentionPolicy> getPolicy(String tenantId, String channel) {
        try {
            return policyRepository.findByTenantIdAndChannel(tenantId, channel);
        } catch (Exception e) {
            log.error("Error fetching retention policy for tenantId={}, channel={}: {}", tenantId, channel, e.getMessage(), e);
            throw new RetentionDataAccessException("Failed to fetch retention policy", e);
        }
    }

    public List<RetentionPolicy> getAllPolicies() {
        try {
            return policyRepository.findAll();
        } catch (Exception e) {
            log.error("Error fetching all retention policies: {}", e.getMessage(), e);
            throw new RetentionDataAccessException("Failed to fetch all retention policies", e);
        }
    }

    public void deleteMessageById(String id) {
        try {
            canonicalMessageRepository.deleteById(id);
        } catch (Exception e) {
            log.error("Error deleting message by id={}: {}", id, e.getMessage(), e);
            throw new RetentionDataAccessException("Failed to delete message by id", e);
        }
    }

    public void processExpiredMessages() {
        log.info("Starting retention policy processing at {}", Instant.now());
        List<RetentionPolicy> allPolicies;
        try {
            allPolicies = policyRepository.findAll();
        } catch (Exception e) {
            log.error("Error fetching all retention policies for processing: {}", e.getMessage(), e);
            throw new RetentionDataAccessException("Failed to fetch all retention policies for processing", e);
        }
        log.info("Found {} policies to process", allPolicies.size());
        for (RetentionPolicy policy : allPolicies) {
            log.info("Processing policy: tenantId={}, channel={}, retentionDays={}",
                    policy.getTenantId(), policy.getChannel(), policy.getRetentionPeriodDays());
            try {
                processMessagesForPolicy(policy);
            } catch (Exception e) {
                log.error("Error processing messages for policy tenantId={}, channel={}: {}", policy.getTenantId(), policy.getChannel(), e.getMessage(), e);
                // Continue processing other policies
            }
        }
        log.info("Completed retention policy processing. Processed {} policies", allPolicies.size());
    }

    private void processMessagesForPolicy(RetentionPolicy policy) {
        List<CanonicalMessage> messages;
        try {
            messages = canonicalMessageRepository.findByTenantIdAndNetwork(policy.getTenantId(), policy.getChannel());
        } catch (Exception e) {
            log.error("Error fetching messages for tenantId={}, channel={}: {}", policy.getTenantId(), policy.getChannel(), e.getMessage(), e);
            throw new RetentionDataAccessException("Failed to fetch messages for policy", e);
        }
        log.info("Query: findByTenantIdAndNetwork(tenantId={}, network={}) returned {} messages",
                policy.getTenantId(), policy.getChannel(), messages.size());
        Instant cutoffDate = Instant.now().minusSeconds(policy.getRetentionPeriodDays() * 86400L);
        if (messages.isEmpty()) {
            saveAuditLog(policy, null, cutoffDate, "NOT_FOUND", "No messages found");
            return;
        }
        int expiredCount = 0;
        for (CanonicalMessage message : messages) {
            if (shouldDeleteMessage(message, cutoffDate)) {
                String messageId = message.getMessageId();
                try {
                    canonicalMessageRepository.deleteById(messageId);
                    boolean fileDeleted = deleteFile(messageId, policy);
                    saveAuditLog(policy, messageId, cutoffDate,
                            fileDeleted ? "DELETED" : "FAILED",
                            fileDeleted ? "Message deleted successfully" : "Failed to delete message");
                    expiredCount++;
                } catch (Exception e) {
                    log.error("Error deleting message or file for messageId={}: {}", messageId, e.getMessage(), e);
                    saveAuditLog(policy, messageId, cutoffDate, "FAILED", "Exception during message deletion: " + e.getMessage());
                }
            }
        }
        if (expiredCount > 0) {
            log.info("Identified {} messages for deletion for tenant {} channel {}",
                    expiredCount, policy.getTenantId(), policy.getChannel());
        } else {
            log.info("No expired messages found for tenant {} channel {}",
                    policy.getTenantId(), policy.getChannel());
        }
    }

    public boolean shouldDeleteMessage(CanonicalMessage message, Instant cutoffDate) {
        return message.getTimestamp().isBefore(cutoffDate);
    }

    private boolean deleteFile(String id, RetentionPolicy policy) {
        boolean deleted = false;
        try {
            Path filePath = Paths.get(fileStorageDirectory, id + ".json");
            if (Files.exists(filePath)) {
                Files.delete(filePath);
                log.info("Message deleted from disk, with file name: {}", id);
                deleted = true;
                saveAuditLog(policy, id, Instant.now(), "DELETED", "Message deleted successfully from disk");
            } else {
                log.warn("File not found: {}", id);
                saveAuditLog(policy, id, Instant.now(), "NOT_FOUND", "File not found on disk");
            }
        } catch (IOException e) {
            log.error("I/O error deleting file for messageId={}: {}", id, e.getMessage(), e);
            saveAuditLog(policy, id, Instant.now(), "FAILED", "I/O error during file deletion: " + e.getMessage());
            throw new RetentionDataAccessException("I/O error deleting file for messageId=" + id, e);
        } catch (Exception e) {
            log.error("Unexpected error deleting file for messageId={}: {}", id, e.getMessage(), e);
            saveAuditLog(policy, id, Instant.now(), "FAILED", "Unexpected error during file deletion: " + e.getMessage());
            throw new RetentionServiceException("Unexpected error deleting file for messageId=" + id, e);
        }
        return deleted;
    }

    private void saveAuditLog(RetentionPolicy policy, String messageId, Instant cutoffDate,
                              String status, String details) {
        RetentionAuditLog logEntry = RetentionAuditLog.builder()
                .tenantId(policy.getTenantId())
                .channel(policy.getChannel())
                .messageId(messageId)
                .processedAt(Instant.now())
                .cutoffDate(cutoffDate)
                .retentionDays(policy.getRetentionPeriodDays())
                .status(status)
                .details(details)
                .build();

        auditLogRepository.save(logEntry);
    }

    // Debug methods
    public void checkDatabaseConnection() {
        try {
            long totalMessages = canonicalMessageRepository.count();
            long totalPolicies = policyRepository.count();

            log.info("=== DATABASE CONNECTION CHECK ===");
            log.info("Total messages found: {}", totalMessages);
            log.info("Total policies found: {}", totalPolicies);

        } catch (Exception e) {
            log.error("Database connection error: {}", e.getMessage());
        }
    }

    public void testFindByTenantIdAndNetwork() {
        try {
            log.info("=== TESTING findByTenantIdAndNetwork ===");

            List<CanonicalMessage> result1 = canonicalMessageRepository.findByTenantIdAndNetwork("bank-001", "email");
            List<CanonicalMessage> result2 = canonicalMessageRepository.findByTenantIdAndNetwork("bank-001", "slack");

            log.info("bank-001 + email: {} messages", result1.size());
            log.info("bank-001 + slack: {} messages", result2.size());

        } catch (Exception e) {
            log.error("Test error: {}", e.getMessage());
        }
    }
}

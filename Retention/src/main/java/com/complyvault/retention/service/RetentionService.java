package com.complyvault.retention.service;

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

        RetentionPolicy savedPolicy = policyRepository.save(policy);

        // Add audit log for policy creation/update
        RetentionAuditLog logEntry = RetentionAuditLog.builder()
                .tenantId(savedPolicy.getTenantId())
                .channel(savedPolicy.getChannel())
                .messageId(null) // No message ID because this is about the policy
                .processedAt(Instant.now())
                .cutoffDate(null) // Not relevant for policy creation
                .retentionDays(savedPolicy.getRetentionPeriodDays())
                .status("POLICY_CREATED_OR_UPDATED")
                .details("Retention policy created or updated for tenant "
                        + savedPolicy.getTenantId() + " and channel "
                        + savedPolicy.getChannel())
                .build();

        auditLogRepository.save(logEntry);

        return savedPolicy;
    }

    public Optional<RetentionPolicy> getPolicy(String tenantId, String channel) {
        return policyRepository.findByTenantIdAndChannel(tenantId, channel);
    }

    public List<RetentionPolicy> getAllPolicies() {
        return policyRepository.findAll();
    }

    public void deleteMessageById(String id) {
        canonicalMessageRepository.deleteById(id);
    }

    public void processExpiredMessages() {
        log.info("Starting retention policy processing at {}", Instant.now());

        List<RetentionPolicy> allPolicies = policyRepository.findAll();
        log.info("Found {} policies to process", allPolicies.size());

        for (RetentionPolicy policy : allPolicies) {
            log.info("Processing policy: tenantId={}, channel={}, retentionDays={}",
                    policy.getTenantId(), policy.getChannel(), policy.getRetentionPeriodDays());
            processMessagesForPolicy(policy);
        }

        log.info("Completed retention policy processing. Processed {} policies", allPolicies.size());
    }

    private void processMessagesForPolicy(RetentionPolicy policy) {
        List<CanonicalMessage> messages = canonicalMessageRepository
                .findByTenantIdAndNetwork(policy.getTenantId(), policy.getChannel());
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
                canonicalMessageRepository.deleteById(messageId);
                boolean fileDeleted = deleteFile(messageId, policy);

                // Save audit log
                saveAuditLog(policy, messageId, cutoffDate,
                        fileDeleted ? "DELETED" : "FAILED",
                        fileDeleted ? "Message deleted successfully" : "Failed to delete message");

                expiredCount++;
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

                // Save audit log for successful deletion
                saveAuditLog(policy, id, Instant.now(), "DELETED", "Message deleted successfully from disk");

            } else {
                log.warn("File not found: {}", id);

                // Save audit log for file not found
                saveAuditLog(policy, id, Instant.now(), "NOT_FOUND", "File not found on disk");
            }
        } catch (IOException e) {
            log.error("Failed to delete file {}: {}", id, e.getMessage());

            // Save audit log for error
            saveAuditLog(policy, id, Instant.now(), "ERROR", "Failed to delete file: " + e.getMessage());
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

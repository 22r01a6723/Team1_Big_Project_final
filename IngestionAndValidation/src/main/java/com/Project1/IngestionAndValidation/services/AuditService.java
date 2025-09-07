package com.Project1.IngestionAndValidation.services;

import com.Project1.IngestionAndValidation.Models.AuditEvent;
import com.Project1.IngestionAndValidation.repository.AuditEventRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

/**
 * AuditService with design patterns:
 * - Factory Pattern: AuditEventFactory creates events
 * - Strategy Pattern: Different log strategies (Default, Duplicate, etc.)
 * - Template Method: logEvent() defines common flow
 */
@Service
public class AuditService {
    private final AuditRepositoryAdapter repoAdapter;

    public AuditService(AuditEventRepository repo) {
        this.repoAdapter = new DatabaseAuditRepositoryAdapter(repo);
    }

    // TEMPLATE METHOD
    public void logEvent(String tenantId,
                         String messageId,
                         String network,
                         String eventType,
                         Map<String, Object> details) {
        AuditLogStrategy strategy = getStrategy(eventType);
        AuditEvent event = strategy.createLog(tenantId, messageId, network, details);
        repoAdapter.save(event);
    }

    public void logDuplicate(String tenantId, String messageId, String network) {
        AuditLogStrategy strategy = new DuplicateLogStrategy();
        AuditEvent event = strategy.createLog(tenantId, messageId, network, Map.of("status", "duplicate detected"));
        repoAdapter.save(event);
    }

    public boolean isDuplicate(String messageId) {
        return repoAdapter.existsByMessageId(messageId);
    }

    // STRATEGY PATTERN
    private AuditLogStrategy getStrategy(String eventType) {
        return switch (eventType) {
            case "DUPLICATE" -> new DuplicateLogStrategy();
            case "VALIDATED" -> new ValidationLogStrategy();
            case "ERROR" -> new ErrorLogStrategy();
            case "INGESTED" -> new IngestionLogStrategy();
            default -> new DefaultLogStrategy();
        };
    }

    interface AuditLogStrategy {
        AuditEvent createLog(String tenantId, String messageId, String network, Map<String, Object> details);
    }

    static class DuplicateLogStrategy implements AuditLogStrategy {
        @Override
        public AuditEvent createLog(String tenantId, String messageId, String network, Map<String, Object> details) {
            return AuditEventFactory.createEvent(tenantId, messageId, network, "DUPLICATE", details);
        }
    }
    static class ValidationLogStrategy implements AuditLogStrategy {
        @Override
        public AuditEvent createLog(String tenantId, String messageId, String network, Map<String, Object> details) {
            return AuditEventFactory.createEvent(tenantId, messageId, network, "VALIDATED", details);
        }
    }
    static class ErrorLogStrategy implements AuditLogStrategy {
        @Override
        public AuditEvent createLog(String tenantId, String messageId, String network, Map<String, Object> details) {
            return AuditEventFactory.createEvent(tenantId, messageId, network, "ERROR", details);
        }
    }
    static class IngestionLogStrategy implements AuditLogStrategy {
        @Override
        public AuditEvent createLog(String tenantId, String messageId, String network, Map<String, Object> details) {
            return AuditEventFactory.createEvent(tenantId, messageId, network, "INGESTED", details);
        }
    }
    static class DefaultLogStrategy implements AuditLogStrategy {
        @Override
        public AuditEvent createLog(String tenantId, String messageId, String network, Map<String, Object> details) {
            return AuditEventFactory.createEvent(tenantId, messageId, network, "DEFAULT", details);
        }
    }

    // FACTORY PATTERN
    static class AuditEventFactory {
        public static AuditEvent createEvent(String tenantId,
                                             String messageId,
                                             String network,
                                             String eventType,
                                             Map<String, Object> details) {
            return AuditEvent.builder()
                    .auditId(UUID.randomUUID().toString())
                    .messageId(messageId)
                    .tenantId(tenantId)
                    .network(network)
                    .eventType(eventType)
                    .timestamp(Instant.now())
                    .performedBy("IngestionAndValidationApp")
                    .details(details)
                    .build();
        }
    }

    // ADAPTER PATTERN
    public interface AuditRepositoryAdapter {
        void save(AuditEvent event);
        boolean existsByMessageId(String messageId);
    }
    public static class DatabaseAuditRepositoryAdapter implements AuditRepositoryAdapter {
        private final AuditEventRepository repo;
        public DatabaseAuditRepositoryAdapter(AuditEventRepository repo) {
            this.repo = repo;
        }
        @Override
        public void save(AuditEvent event) {
            repo.save(event);
        }
        @Override
        public boolean existsByMessageId(String messageId) {
            return repo.existsByMessageId(messageId);
        }
    }
}

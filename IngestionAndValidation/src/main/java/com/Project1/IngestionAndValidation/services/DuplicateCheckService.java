package com.Project1.IngestionAndValidation.services;

import com.Project1.IngestionAndValidation.repository.UniqueIdRepository;
import org.springframework.stereotype.Service;

/**
 * DuplicateCheckService with design patterns:
 * - Strategy Pattern: multiple ways to check duplicates
 * - Factory/Context: select which strategy to use
 */
@Service
public class DuplicateCheckService {

    private final DuplicateCheckContext duplicateCheckContext;

    public DuplicateCheckService(UniqueIdRepository uniqueIdRepository) {
        // For now, default strategy = check by messageId
        this.duplicateCheckContext = new DuplicateCheckContext(
                new MessageIdDuplicateStrategy(uniqueIdRepository)
        );
    }

    public boolean isDuplicate(String messageId) {
        return duplicateCheckContext.check(messageId);
    }

    // STRATEGY PATTERN

    interface DuplicateCheckStrategy {
        boolean isDuplicate(String key);
    }

    static class MessageIdDuplicateStrategy implements DuplicateCheckStrategy {
        private final UniqueIdRepository repo;

        public MessageIdDuplicateStrategy(UniqueIdRepository repo) {
            this.repo = repo;
        }

        @Override
        public boolean isDuplicate(String messageId) {
            return repo.existsById(messageId);
        }
    }

    // Example extension: check duplicates by hash of payload
    static class PayloadHashDuplicateStrategy implements DuplicateCheckStrategy {
        @Override
        public boolean isDuplicate(String payloadHash) {
            // future: implement DB check for payload hash
            return false;
        }
    }

    // CONTEXT (STRATEGY SELECTOR)

    static class DuplicateCheckContext {
        private DuplicateCheckStrategy strategy;

        public DuplicateCheckContext(DuplicateCheckStrategy strategy) {
            this.strategy = strategy;
        }

        public void setStrategy(DuplicateCheckStrategy strategy) {
            this.strategy = strategy;
        }

        public boolean check(String key) {
            return strategy.isDuplicate(key);
        }
    }
}

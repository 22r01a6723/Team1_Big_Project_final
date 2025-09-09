/*package com.project_1.normalizer.service;


import com.project_1.normalizer.model.CanonicalMessage;
import com.project_1.normalizer.repository.MessageRepository;
import com.project_1.normalizer.util.MessageAdapterFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Slf4j
@Service
public class MongoStorageService implements StorageService {


    private final MessageRepository messageRepository;



    public MongoStorageService(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

    public void store(CanonicalMessage message,String raw) {
        try {
            messageRepository.save(message);
            log.info("Message saved in MongoDB successfully"+message.getMessageId());
        }
        catch (Exception e) {
            log.error(e.getMessage());
        }
    }


    public boolean isDuplicate(String Id) throws IOException {
        System.out.println(Id);
        if (messageRepository.existsById(Id)) {
            log.warn("Dropped message with uuid={} because duplicate found", Id);
            return true;
        }
        return false;
    }

}*/

package com.project_1.normalizer.service;

import com.complyvault.shared.client.AuditClient;
import com.project_1.normalizer.exception.NormalizerStorageException;
import com.project_1.normalizer.model.CanonicalMessage;
import com.project_1.normalizer.repository.MessageRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.Instant;
import java.util.Map;

@Slf4j
@Service
public class MongoStorageService implements StorageService, IMongoStorageService {
    private final MessageRepository messageRepository;
    private final AuditClient auditClient;

    public MongoStorageService(MessageRepository messageRepository, AuditClient auditClient) {
        this.messageRepository = messageRepository;
        this.auditClient = auditClient;
    }

    @Override
    public void store(CanonicalMessage message, String raw) {
        try {
            message.setCreatedAt(Instant.now());
            messageRepository.save(message);
            log.info("Message saved in MongoDB successfully {}", message.getMessageId());
            auditClient.logEvent(
                message.getTenantId(),
                message.getMessageId(),
                message.getNetwork(),
                "STORED_MONGODB",
                "normalizer-service",
                Map.of(
                    "collection", "messages",
                    "documentId", message.getMessageId()
                )
            );
        } catch (Exception e) {
            log.error("Mongo save failed for {}: {}", message.getMessageId(), e.getMessage());
            auditClient.logEvent(
                message.getTenantId(),
                message.getMessageId(),
                message.getNetwork(),
                "STORE_MONGODB_FAILED",
                "normalizer-service",
                Map.of("error", e.getMessage())
            );
            throw new NormalizerStorageException("Failed to store message in MongoDB for messageId=" + message.getMessageId(), e);
        }
    }

    @Override
    public boolean isDuplicate(String Id) {
        if (Id == null) return false;
        if (messageRepository.existsById(Id)) {
            log.warn("Dropped message with uuid={} because duplicate found", Id);
            return true;
        }
        return false;
    }
}

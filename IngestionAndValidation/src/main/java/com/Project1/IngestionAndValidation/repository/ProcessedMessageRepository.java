// repositories/ProcessedMessageRepository.java
package com.Project1.IngestionAndValidation.repository;

import com.Project1.IngestionAndValidation.Models.ProcessedMessage;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ProcessedMessageRepository extends MongoRepository<ProcessedMessage, String> {
    boolean existsById(String id);
}

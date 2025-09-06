// services/DuplicateCheckService.java
package com.Project1.IngestionAndValidation.services;

import com.Project1.IngestionAndValidation.Models.ProcessedMessage;
import com.Project1.IngestionAndValidation.Models.UniqueId;
import com.Project1.IngestionAndValidation.repository.ProcessedMessageRepository;
import com.Project1.IngestionAndValidation.repository.UniqueIdRepository;
import org.springframework.stereotype.Service;

@Service
public class DuplicateCheckService {

    private final UniqueIdRepository uniqueIdRepository;

    public DuplicateCheckService(UniqueIdRepository uniqueIdRepository) {
        this.uniqueIdRepository = uniqueIdRepository;
    }

    public boolean isDuplicate(String messageId) {
        return uniqueIdRepository.existsById(messageId);
    }
}

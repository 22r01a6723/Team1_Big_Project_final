//// services/DuplicateCheckService.java
//package com.Project1.IngestionAndValidation.services;
//
//import com.Project1.IngestionAndValidation.Models.ProcessedMessage;
//import com.Project1.IngestionAndValidation.Models.UniqueId;
//import com.Project1.IngestionAndValidation.repository.ProcessedMessageRepository;
//import com.Project1.IngestionAndValidation.repository.UniqueIdRepository;
//import org.springframework.stereotype.Service;
//
//@Service
//public class DuplicateCheckService {
//
//    private final UniqueIdRepository uniqueIdRepository;
//
//    public DuplicateCheckService(UniqueIdRepository uniqueIdRepository) {
//        this.uniqueIdRepository = uniqueIdRepository;
//    }
//
//    public boolean isDuplicate(String messageId) {
//        try {
//            return uniqueIdRepository.existsById(messageId);
//        } catch (DataAccessException e) {
//            throw new CompanyVaultPersistenceException("Failed to check duplicate for messageId=" + messageId, e);
//        }
//    }
//
//}
package com.Project1.IngestionAndValidation.services;

import com.Project1.IngestionAndValidation.Models.UniqueId;
import com.Project1.IngestionAndValidation.exception.CompanyVaultPersistenceException;
import com.Project1.IngestionAndValidation.exception.DataAccessException;
import com.Project1.IngestionAndValidation.repository.UniqueIdRepository;
import org.springframework.stereotype.Service;

@Service
public class DuplicateCheckService {

    private final UniqueIdRepository uniqueIdRepository;

    public DuplicateCheckService(UniqueIdRepository uniqueIdRepository) {
        this.uniqueIdRepository = uniqueIdRepository;
    }

    public boolean isDuplicate(String messageId) {
        try {
            if (uniqueIdRepository.existsById(messageId)) {
                return true; // duplicate
            }
            uniqueIdRepository.save(new UniqueId(messageId));
            return false;
        } catch (Exception e) {
            throw new DataAccessException("Failed to check/save duplicate for messageId=" + messageId, e);
        }
    }
}

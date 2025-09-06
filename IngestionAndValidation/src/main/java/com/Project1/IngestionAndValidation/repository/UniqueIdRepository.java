package com.Project1.IngestionAndValidation.repository;

import com.Project1.IngestionAndValidation.Models.UniqueId;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UniqueIdRepository extends MongoRepository<UniqueId, String> {
}


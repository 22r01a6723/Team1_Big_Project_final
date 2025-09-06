package com.project_1.normalizer.repository;

import com.project_1.normalizer.model.CanonicalMessage;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface MessageRepository extends MongoRepository<CanonicalMessage, String> {
}


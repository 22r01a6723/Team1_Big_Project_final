package com.project_1.normalizer.repository;

import com.project_1.normalizer.model.UniqueId;
import org.springframework.data.mongodb.repository.MongoRepository;
public interface UniqueIdRepository extends MongoRepository<UniqueId, String> {

}

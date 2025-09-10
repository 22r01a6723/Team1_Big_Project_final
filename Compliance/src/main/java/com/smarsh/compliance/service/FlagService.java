package com.smarsh.compliance.service;

import com.smarsh.compliance.entity.Flag;
import com.smarsh.compliance.repository.FlagRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class FlagService {

    private final FlagRepository flagRepository;

    public FlagService(FlagRepository flagRepository) {
        this.flagRepository = flagRepository;
    }

    public Flag saveFlag(Flag flag) {
        try {
            Flag saved = flagRepository.save(flag);
            log.info("Flag saved: {}", saved.getFlagId());
            return saved;
        } catch (Exception e) {
            log.error("Error saving flag", e);
            throw new com.smarsh.compliance.exception.ComplianceMongoException("Error saving flag", e);
        }
    }
}

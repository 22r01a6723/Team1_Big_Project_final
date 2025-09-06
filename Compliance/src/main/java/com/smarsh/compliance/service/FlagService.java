package com.smarsh.compliance.service;

import com.smarsh.compliance.entity.Flag;
import com.smarsh.compliance.repository.FlagRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class FlagService {
    private FlagRepository flagRepository;

    public FlagService(FlagRepository flagRepository) {
        this.flagRepository = flagRepository;
    }

    public void saveFlag(Flag flag) {
        Flag savedFlag = flagRepository.save(flag);
        log.info("Flag saved in DB");
//        kafkaProducerService.publishFlag(savedFlag);
//        log.info("Flag {} saved to topic {}", savedFlag.getMessageId().toString(), flag.getMessageId().toString());
    }
}
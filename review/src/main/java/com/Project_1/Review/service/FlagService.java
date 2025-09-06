package com.Project_1.Review.service;

import com.Project_1.Review.entity.Flag;
import com.Project_1.Review.repository.FlagRepository;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service

public class FlagService {

    private final FlagRepository flagRepository;

    public FlagService(FlagRepository flagRepository) {
        this.flagRepository = flagRepository;
    }

    public List<Flag> getAllFlags(String tenantId, String network) {
        return flagRepository.findByTenantIdAndNetwork(tenantId, network);
    }

    public List<Flag> getFlagsByMessageId(String messageId, String tenantId, String network) {
        return flagRepository.findByMessageIdAndTenantIdAndNetwork(messageId, tenantId, network);
    }

    public List<Flag> getAllFlags(String flagId, String tenantId, String network) {
        return flagRepository.findByFlagIdAndTenantIdAndNetwork(flagId, tenantId, network);
    }

    // this is the edited code.
    public List<Flag> getFlagsByMessage(String tenantId, String network) {
        return flagRepository.findByTenantIdAndNetwork(tenantId, network);
    }

    public List<Flag> getFlagsByRuleId(String ruleId, String tenantId, String network) {
        return flagRepository.findByRuleIdAndTenantIdAndNetwork(ruleId, tenantId, network);
    }

    public List<Flag> searchFlagsByDescription(String description, String tenantId, String network) {
        return flagRepository.findByFlagDescriptionContainingIgnoreCaseAndTenantIdAndNetwork(description, tenantId, network);
    }

//    public List<Flag> searchFlagsByDescription(String keyword, String tenantId, String network) {
//        return flagRepository.findByFlagDescriptionContainingIgnoreCaseAndTenantIdAndNetwork(keyword, tenantId, network);
//    }

    public List<Flag> getFlagsByDateRange(Long start, Long end, String tenantId, String network) {
        return flagRepository.findByCreatedAtBetweenAndTenantIdAndNetwork(start, end, tenantId, network);
    }
}


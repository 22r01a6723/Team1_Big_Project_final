package com.smarsh.compliance.service;

import com.smarsh.compliance.entity.Flag;
import com.smarsh.compliance.entity.Policy;
import com.smarsh.compliance.exception.CompliancePolicyException;
import com.smarsh.compliance.repository.FlagRepository;
import com.smarsh.compliance.repository.PolicyRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class PolicyService {
    private final FlagRepository flagRepository;
    private PolicyRepository policyRepository;

    public PolicyService(PolicyRepository policyRepository,FlagRepository flagRepository) {
        this.policyRepository = policyRepository;
        this.flagRepository=flagRepository;
    }

    public List<Policy>  getPoliciesByIds(List<String> policyIds) {
        try {
            List<Policy> policies = new ArrayList<>();
            policyIds.forEach(policyId -> {
                Optional<Policy> policy = policyRepository.findById(policyId);
                policy.ifPresent(policies::add);
            });
            return policies;
        } catch (Exception e) {
            log.error("Error fetching policies by IDs", e);
            throw new CompliancePolicyException("Error fetching policies by IDs: " + e.getMessage(), e);
        }
    }

    public void addPolicy(Policy policy) {
        try {
            policyRepository.save(policy);
        } catch (Exception e) {
            log.error("Error in saving policy", e);
            throw new CompliancePolicyException("Error saving policy: " + e.getMessage(), e);
        }
    }

    public List<Policy> getAllPolicies() {
        try {
            return policyRepository.findAll();
        } catch (Exception e) {
            log.error("Error fetching all policies", e);
            throw new CompliancePolicyException("Error fetching all policies: " + e.getMessage(), e);
        }
    }

}

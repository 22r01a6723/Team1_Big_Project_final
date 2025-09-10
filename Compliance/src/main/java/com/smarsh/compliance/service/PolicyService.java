package com.smarsh.compliance.service;

import com.smarsh.compliance.entity.Policy;
import com.smarsh.compliance.exception.CompliancePolicyException;
import com.smarsh.compliance.repository.PolicyRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class PolicyService {

    private final PolicyRepository policyRepository;

    public PolicyService(PolicyRepository policyRepository) {
        this.policyRepository = policyRepository;
    }

    public List<Policy> getPoliciesByIds(List<String> policyIds) {
        try {
            List<Policy> out = new ArrayList<>();
            for (String id : policyIds) {
                Optional<Policy> p = policyRepository.findById(id);
                p.ifPresent(out::add);
            }
            return out;
        } catch (Exception e) {
            log.error("Error fetching policies by IDs", e);
            throw new CompliancePolicyException("Error fetching policies by IDs: " + e.getMessage(), e);
        }
    }

    public Policy addPolicy(Policy policy) {
        try {
            return policyRepository.save(policy);
        } catch (Exception e) {
            log.error("Error saving policy", e);
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

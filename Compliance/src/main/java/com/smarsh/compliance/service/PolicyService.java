package com.smarsh.compliance.service;

import com.smarsh.compliance.entity.Flag;
import com.smarsh.compliance.entity.Policy;
import com.smarsh.compliance.repository.FlagRepository;
import com.smarsh.compliance.repository.PolicyRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

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
        List<Policy> policies=new ArrayList<>();
        policyIds.forEach(policyId->{
            Optional<Policy> policy=policyRepository.findById(policyId);
            policy.ifPresent(policies::add);
        });
        return policies;
    }


    public ResponseEntity<String> addPolicy(Policy policy) {
        try {
            policyRepository.save(policy);
        }
        catch (Exception e) {
            log.info("Error in saving policy",e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
        return ResponseEntity.ok("Policy added successfully");
    }

    public List<Policy> getAllPolicies(){
        return policyRepository.findAll();
    }


}

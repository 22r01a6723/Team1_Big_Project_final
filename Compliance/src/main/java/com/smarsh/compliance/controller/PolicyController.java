package com.smarsh.compliance.controller;

import com.smarsh.compliance.entity.Policy;
import com.smarsh.compliance.service.PolicyService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
public class PolicyController {

    private final PolicyService policyService;

    public PolicyController(PolicyService policyService) {
        this.policyService = policyService;
    }

    @PostMapping("/api/public/policy")
    public ResponseEntity<String> createPolicy(@RequestBody Policy policy) {
        log.info("Creating policy {}", policy.toString());
        policyService.addPolicy(policy);
        return ResponseEntity.ok("Policy created successfully");
    }

    @GetMapping("/api/public/policy")
    public ResponseEntity<List<Policy>> getAllPolicies() {
        List<Policy> policies = policyService.getAllPolicies();
        return ResponseEntity.ok(policies);
    }

}

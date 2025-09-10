package com.smarsh.compliance.controller;

import com.smarsh.compliance.entity.Policy;
import com.smarsh.compliance.service.PolicyService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/public/policy")
public class PolicyController {

    private final PolicyService policyService;

    public PolicyController(PolicyService policyService) {
        this.policyService = policyService;
    }

    @PostMapping
    public ResponseEntity<String> createPolicy(@RequestBody Policy policy) {
        log.info("Creating policy {}", policy);
        policyService.addPolicy(policy);
        return ResponseEntity.ok("Policy created successfully");
    }

    @GetMapping
    public ResponseEntity<List<Policy>> getAllPolicies() {
        List<Policy> policies = policyService.getAllPolicies();
        return ResponseEntity.ok(policies);
    }
}

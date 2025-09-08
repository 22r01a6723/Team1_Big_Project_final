package com.complyvault.retention.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.complyvault.retention.model.RetentionPolicy;
import com.complyvault.retention.service.RetentionService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/retention-policies")
@RequiredArgsConstructor
public class RetentionPolicyController {

    private final RetentionService retentionService;

    @PostMapping
    public ResponseEntity<RetentionPolicy> createOrUpdatePolicy(@RequestBody RetentionPolicy policy) {
        RetentionPolicy savedPolicy = retentionService.saveOrUpdatePolicy(policy);
        return ResponseEntity.ok(savedPolicy);
    }

    @GetMapping("/{tenantId}/{channel}")
    public ResponseEntity<RetentionPolicy> getPolicy(
            @PathVariable String tenantId,
            @PathVariable String channel) {

        Optional<RetentionPolicy> policy = retentionService.getPolicy(tenantId, channel);
        return policy.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<RetentionPolicy>> getAllPolicies() {
        List<RetentionPolicy> policies = retentionService.getAllPolicies();
        return ResponseEntity.ok(policies);
    }

    @PostMapping("/process-expired")
    public ResponseEntity<String> triggerManualProcessing() {
        System.out.println("reachedhere part1");
        retentionService.processExpiredMessages();
        return ResponseEntity.ok("Retention processing triggered manually");
    }

    // Add debug endpoint
    @GetMapping("/check-db")
    public ResponseEntity<Map<String, Object>> checkDatabaseConnection() {
        retentionService.checkDatabaseConnection();
        retentionService.testFindByTenantIdAndNetwork();
        
        Map<String, Object> response = new HashMap<>();
        response.put("status", "Database check completed - check logs for details");
        return ResponseEntity.ok(response);
    }
}

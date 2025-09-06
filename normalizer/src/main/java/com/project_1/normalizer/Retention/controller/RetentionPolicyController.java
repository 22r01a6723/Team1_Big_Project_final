package com.project_1.normalizer.Retention.controller;

import com.project_1.normalizer.Retention.model.RetentionPolicy;
import com.project_1.normalizer.Retention.service.RetentionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/retention-policies")
public class RetentionPolicyController {

    private final RetentionService retentionService;

    public RetentionPolicyController(RetentionService retentionService) {
        this.retentionService = retentionService;
    }


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
    public ResponseEntity<String> checkDatabase() {
        retentionService.checkDatabaseConnection();
        return ResponseEntity.ok("Database check completed. Check logs.");
    }

    @GetMapping("/debug-all")
    public ResponseEntity<String> debugAll() {
//        retentionService.debugAllData();
        return ResponseEntity.ok("Debug all data completed. Check logs.");
    }

    @GetMapping("/test-queries")
    public ResponseEntity<String> testQueries() {
//        retentionService.testSimpleQueries();
        return ResponseEntity.ok("Query tests completed. Check logs.");
    }

    @GetMapping("/collection-names")
    public ResponseEntity<Map<String, String>> getCollectionNames() {
        Map<String, String> collections = new HashMap<>();
        collections.put("messages", "CanonicalMessage collection");
        collections.put("retention_policies", "RetentionPolicy collection");
        return ResponseEntity.ok(collections);
    }
}


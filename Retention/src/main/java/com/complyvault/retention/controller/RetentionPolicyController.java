package com.complyvault.retention.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.complyvault.retention.exception.RetentionNotFoundException;
import com.complyvault.retention.exception.RetentionServiceException;
import com.complyvault.retention.model.RetentionPolicy;
import com.complyvault.retention.service.RetentionService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api/retention-policies")
@RequiredArgsConstructor
public class RetentionPolicyController {

    private final RetentionService retentionService;

    private static final Logger logger = LoggerFactory.getLogger(RetentionPolicyController.class);

    @PostMapping
    public ResponseEntity<RetentionPolicy> createOrUpdatePolicy(@Valid @RequestBody RetentionPolicy policy) {
        logger.info("POST /api/retention-policies called for tenantId={}, channel={}", policy.getTenantId(), policy.getChannel());
        if (policy.getTenantId() == null || policy.getTenantId().trim().isEmpty()) {
            throw new IllegalArgumentException("tenantId must not be null or empty");
        }
        if (policy.getChannel() == null || policy.getChannel().trim().isEmpty()) {
            throw new IllegalArgumentException("channel must not be null or empty");
        }
        if (policy.getRetentionPeriodDays() < 1) {
            throw new IllegalArgumentException("retentionPeriodDays must be at least 1");
        }
        RetentionPolicy savedPolicy = retentionService.saveOrUpdatePolicy(policy);
        return ResponseEntity.ok(savedPolicy);
    }

    @GetMapping("/{tenantId}/{channel}")
    public ResponseEntity<RetentionPolicy> getPolicy(
            @PathVariable String tenantId,
            @PathVariable String channel) {
        logger.info("GET /api/retention-policies/{}/{} called", tenantId, channel);
        if (tenantId == null || tenantId.trim().isEmpty() || channel == null || channel.trim().isEmpty()) {
            throw new IllegalArgumentException("tenantId and channel must not be null or empty");
        }
        Optional<RetentionPolicy> policy = retentionService.getPolicy(tenantId, channel);
        if (policy.isPresent()) {
            return ResponseEntity.ok(policy.get());
        } else {
            throw new RetentionNotFoundException("Retention policy not found for tenantId=" + tenantId + ", channel=" + channel);
        }
    }

    @GetMapping
    public ResponseEntity<List<RetentionPolicy>> getAllPolicies() {
        logger.info("GET /api/retention-policies called");
        List<RetentionPolicy> policies = retentionService.getAllPolicies();
        return ResponseEntity.ok(policies);
    }

    @PostMapping("/process-expired")
    public ResponseEntity<String> triggerManualProcessing() {
        logger.info("POST /api/retention-policies/process-expired called");
        try {
            retentionService.processExpiredMessages();
        } catch (Exception e) {
            logger.error("Error during manual retention processing: {}", e.getMessage(), e);
            throw new RetentionServiceException("Error during manual retention processing: " + e.getMessage(), e);
        }
        return ResponseEntity.ok("Retention processing triggered manually");
    }

    @GetMapping("/check-db")
    public ResponseEntity<Map<String, Object>> checkDatabaseConnection() {
        logger.info("GET /api/retention-policies/check-db called");
        try {
            retentionService.checkDatabaseConnection();
            retentionService.testFindByTenantIdAndNetwork();
        } catch (Exception e) {
            logger.error("Error during DB check: {}", e.getMessage(), e);
            throw new RetentionServiceException("Error during DB check: " + e.getMessage(), e);
        }
        Map<String, Object> response = new HashMap<>();
        response.put("status", "Database check completed - check logs for details");
        return ResponseEntity.ok(response);
    }
}

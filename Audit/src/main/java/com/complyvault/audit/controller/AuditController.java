package com.complyvault.audit.controller;

import com.complyvault.audit.model.AuditEvent;
import com.complyvault.audit.service.AuditService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/audit")
@RequiredArgsConstructor
public class AuditController {

    private final AuditService auditService;

    @PostMapping("/log")
    public ResponseEntity<String> logEvent(@RequestBody AuditEventRequest request) {
        auditService.logEvent(
                request.getTenantId(),
                request.getMessageId(),
                request.getNetwork(),
                request.getEventType(),
                request.getPerformedBy(),
                request.getServiceName(),
                request.getDetails()
        );
        return ResponseEntity.ok("Audit event logged successfully");
    }

    @GetMapping("/tenant/{tenantId}")
    public ResponseEntity<List<AuditEvent>> getAuditEventsByTenant(@PathVariable String tenantId) {
        List<AuditEvent> events = auditService.getAuditEventsByTenant(tenantId);
        return ResponseEntity.ok(events);
    }

    @GetMapping("/message/{messageId}")
    public ResponseEntity<List<AuditEvent>> getAuditEventsByMessageId(@PathVariable String messageId) {
        List<AuditEvent> events = auditService.getAuditEventsByMessageId(messageId);
        return ResponseEntity.ok(events);
    }

    @GetMapping("/event-type/{eventType}")
    public ResponseEntity<List<AuditEvent>> getAuditEventsByEventType(@PathVariable String eventType) {
        List<AuditEvent> events = auditService.getAuditEventsByEventType(eventType);
        return ResponseEntity.ok(events);
    }

    @GetMapping("/service/{serviceName}")
    public ResponseEntity<List<AuditEvent>> getAuditEventsByService(@PathVariable String serviceName) {
        List<AuditEvent> events = auditService.getAuditEventsByService(serviceName);
        return ResponseEntity.ok(events);
    }

    @GetMapping("/time-range")
    public ResponseEntity<List<AuditEvent>> getAuditEventsByTimeRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant startTime,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant endTime) {
        List<AuditEvent> events = auditService.getAuditEventsByTimeRange(startTime, endTime);
        return ResponseEntity.ok(events);
    }

    @GetMapping("/tenant/{tenantId}/time-range")
    public ResponseEntity<List<AuditEvent>> getAuditEventsByTenantAndTimeRange(
            @PathVariable String tenantId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant startTime,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant endTime) {
        List<AuditEvent> events = auditService.getAuditEventsByTenantAndTimeRange(tenantId, startTime, endTime);
        return ResponseEntity.ok(events);
    }

    @GetMapping("/search")
    public ResponseEntity<List<AuditEvent>> searchAuditEvents(
            @RequestParam String tenantId,
            @RequestParam String eventType,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant startTime,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant endTime) {
        List<AuditEvent> events = auditService.searchAuditEvents(tenantId, eventType, startTime, endTime);
        return ResponseEntity.ok(events);
    }

    @GetMapping("/duplicate/{messageId}")
    public ResponseEntity<Map<String, Boolean>> checkDuplicate(@PathVariable String messageId) {
        boolean isDuplicate = auditService.isDuplicate(messageId);
        return ResponseEntity.ok(Map.of("isDuplicate", isDuplicate));
    }

    // Request DTO for logging events
    public static class AuditEventRequest {
        private String tenantId;
        private String messageId;
        private String network;
        private String eventType;
        private String performedBy;
        private String serviceName;
        private Map<String, Object> details;

        // Getters and setters
        public String getTenantId() { return tenantId; }
        public void setTenantId(String tenantId) { this.tenantId = tenantId; }
        
        public String getMessageId() { return messageId; }
        public void setMessageId(String messageId) { this.messageId = messageId; }
        
        public String getNetwork() { return network; }
        public void setNetwork(String network) { this.network = network; }
        
        public String getEventType() { return eventType; }
        public void setEventType(String eventType) { this.eventType = eventType; }
        
        public String getPerformedBy() { return performedBy; }
        public void setPerformedBy(String performedBy) { this.performedBy = performedBy; }
        
        public String getServiceName() { return serviceName; }
        public void setServiceName(String serviceName) { this.serviceName = serviceName; }
        
        public Map<String, Object> getDetails() { return details; }
        public void setDetails(Map<String, Object> details) { this.details = details; }
    }
}

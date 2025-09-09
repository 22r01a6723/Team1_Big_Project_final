package com.complyvault.retention.config;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.complyvault.retention.service.RetentionService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ScheduledTasksConfig {

    private static final Logger logger = LoggerFactory.getLogger(ScheduledTasksConfig.class);
    private final RetentionService retentionService;

    @Scheduled(cron = "0 0 1 * * ?") // Every day at 1:00 AM
    public void processRetentionPolicies() {
        try {
            retentionService.processExpiredMessages();
        } catch (Exception e) {
            // Log the error with context for monitoring/alerting
            logger.error("[ScheduledTasksConfig] Error during scheduled retention processing", e);
            // Optionally: send alert/notification here
        }
    }
}

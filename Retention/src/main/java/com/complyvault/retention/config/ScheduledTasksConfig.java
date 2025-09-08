package com.complyvault.retention.config;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.complyvault.retention.service.RetentionService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ScheduledTasksConfig {

    private final RetentionService retentionService;

    @Scheduled(cron = "0 0 1 * * ?") // Every day at 1:00 AM
    public void processRetentionPolicies() {
        retentionService.processExpiredMessages();
    }
}

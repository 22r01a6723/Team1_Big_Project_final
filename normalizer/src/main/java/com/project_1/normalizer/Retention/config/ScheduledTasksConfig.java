package com.project_1.normalizer.Retention.config;

import com.project_1.normalizer.Retention.service.RetentionService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ScheduledTasksConfig {

    private final RetentionService retentionService;

    @Scheduled(cron = "0 0 1 * * ?") // Every day at 1:00 AM
    public void processRetentionPolicies() {
        retentionService.processExpiredMessages();
    }
}
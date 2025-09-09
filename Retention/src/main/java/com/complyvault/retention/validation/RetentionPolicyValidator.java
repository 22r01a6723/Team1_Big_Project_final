package com.complyvault.retention.validation;

import com.complyvault.retention.model.RetentionPolicy;
import com.complyvault.retention.exception.RetentionServiceException;
import org.springframework.stereotype.Component;

@Component
public class RetentionPolicyValidator {
    public void validate(RetentionPolicy policy) {
        if (policy.getTenantId() == null || policy.getTenantId().trim().isEmpty()) {
            throw new RetentionServiceException("tenantId must not be null or empty");
        }
        if (policy.getChannel() == null || policy.getChannel().trim().isEmpty()) {
            throw new RetentionServiceException("channel must not be null or empty");
        }
        if (policy.getRetentionPeriodDays() < 1) {
            throw new RetentionServiceException("retentionPeriodDays must be at least 1");
        }
    }
}


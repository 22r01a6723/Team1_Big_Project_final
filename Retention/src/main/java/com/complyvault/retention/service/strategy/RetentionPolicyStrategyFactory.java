package com.complyvault.retention.service.strategy;

import com.complyvault.retention.model.RetentionPolicy;

public class RetentionPolicyStrategyFactory {
    public RetentionPolicyStrategy getStrategy(RetentionPolicy policy) {
        // Extend this logic to return different strategies based on policy/channel/tenant
        return new DefaultRetentionPolicyStrategy();
    }
}


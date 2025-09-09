package com.complyvault.retention.service.strategy;

import com.complyvault.retention.model.RetentionPolicy;
import com.complyvault.retention.model.CanonicalMessage;
import java.util.List;

public interface RetentionPolicyStrategy {
    void processExpiredMessages(List<CanonicalMessage> messages, RetentionPolicy policy);
}


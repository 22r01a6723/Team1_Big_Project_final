package com.complyvault.retention.service.strategy;

import com.complyvault.retention.model.RetentionPolicy;
import com.complyvault.retention.model.CanonicalMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;

public class DefaultRetentionPolicyStrategy implements RetentionPolicyStrategy {
    private static final Logger logger = LoggerFactory.getLogger(DefaultRetentionPolicyStrategy.class);

    @Override
    public void processExpiredMessages(List<CanonicalMessage> messages, RetentionPolicy policy) {
        // Default logic for processing expired messages
        for (CanonicalMessage message : messages) {
            // Example: log message deletion
            logger.info("Deleting message {} for tenant {} and channel {} due to retention policy {} days.",
                    message.getMessageId(), policy.getTenantId(), policy.getChannel(), policy.getRetentionPeriodDays());
            // Actual deletion logic would go here
        }
    }
}

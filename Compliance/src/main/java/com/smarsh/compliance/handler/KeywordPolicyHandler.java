package com.smarsh.compliance.handler;

import com.complyvault.shared.client.AuditClient;
import com.smarsh.compliance.entity.Flag;
import com.smarsh.compliance.entity.KeywordPolicy;
import com.smarsh.compliance.entity.Policy;
import com.smarsh.compliance.models.Message;
import com.smarsh.compliance.service.PolicyMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Component
public class KeywordPolicyHandler extends AbstractPolicyHandler {

    private final AuditClient auditClient;

    public KeywordPolicyHandler(AuditClient auditClient) {
        this.auditClient = auditClient;
    }

    @Override
    public boolean supports(String type) {
        return "keyword".equalsIgnoreCase(type) || "KEYWORD".equalsIgnoreCase(type);
    }

    @Override
    public Optional<Flag> handle(Message message, Policy policy) {
        try {
            if (!supports(policy.getType())) {
                // not this handler's responsibility — continue chain
                return superHandle(message, policy);
            }

            String fieldValue = getFieldValue(message, policy.getField());
            if (fieldValue == null) {
                return superHandle(message, policy);
            }

            KeywordPolicy kp = PolicyMapper.getKeywordPolicy(policy);
            List<String> keywords = kp.getKeywords();
            for (String keyword : keywords) {
                if (fieldValue.contains(keyword)) {
                    Flag flag = new Flag(policy.getRuleId(), message.getMessageId(),
                            policy.getDescription(), message.getNetwork(), message.getTenantId());
                    // audit and return
                    auditClient.logEvent(message.getTenantId(), message.getMessageId(), message.getNetwork(),
                            "MESSAGE_FLAGGED", "compliance-service", Map.of("flag", flag));
                    // Save audited flag handling occurs in service code
                    // continue chain (we still call super to allow other handlers to run)
                    superHandle(message, policy);
                    return Optional.of(flag);
                }
            }

            return superHandle(message, policy);
        } catch (Exception e) {
            log.error("Error in KeywordPolicyHandler", e);
            return superHandle(message, policy);
        }
    }

    private String getFieldValue(Message message, String field) {
        if ("subject".equalsIgnoreCase(field)) {
            return message.getContent() == null ? null : message.getContent().getSubject();
        } else if ("body".equalsIgnoreCase(field)) {
            return message.getContent() == null ? null : message.getContent().getBody();
        }
        return null;
    }
}

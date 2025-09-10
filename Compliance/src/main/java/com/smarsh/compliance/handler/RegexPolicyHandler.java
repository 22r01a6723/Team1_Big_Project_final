package com.smarsh.compliance.handler;

import com.complyvault.shared.client.AuditClient;
import com.smarsh.compliance.entity.Flag;
import com.smarsh.compliance.entity.Policy;
import com.smarsh.compliance.entity.RegexPolicy;
import com.smarsh.compliance.models.Message;
import com.smarsh.compliance.service.PolicyMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Component
public class RegexPolicyHandler extends AbstractPolicyHandler {

    private final AuditClient auditClient;

    public RegexPolicyHandler(AuditClient auditClient) {
        this.auditClient = auditClient;
    }

    @Override
    public boolean supports(String type) {
        return "regex".equalsIgnoreCase(type) || "REGEX".equalsIgnoreCase(type);
    }

    @Override
    public Optional<Flag> handle(Message message, Policy policy) {
        try {
            if (!supports(policy.getType())) {
                return superHandle(message, policy);
            }

            String fieldValue = getFieldValue(message, policy.getField());
            if (fieldValue == null) {
                return superHandle(message, policy);
            }

            RegexPolicy rp = PolicyMapper.getRegexPolicy(policy);
            try {
                Pattern pattern = Pattern.compile(rp.getPattern());
                Matcher matcher = pattern.matcher(fieldValue);
                if (matcher.find()) {
                    Flag flag = new Flag(policy.getRuleId(), message.getMessageId(),
                            policy.getDescription(), message.getNetwork(), message.getTenantId());
                    auditClient.logEvent(message.getTenantId(), message.getMessageId(), message.getNetwork(),
                            "MESSAGE_FLAGGED", "compliance-service", Map.of("flag", flag));
                    // still continue chain so other handlers also run
                    superHandle(message, policy);
                    return Optional.of(flag);
                }
            } catch (Exception rex) {
                log.warn("Invalid regex in policy {}: {}", policy.getRuleId(), rp.getPattern());
            }

            return superHandle(message, policy);
        } catch (Exception e) {
            log.error("Error in RegexPolicyHandler", e);
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

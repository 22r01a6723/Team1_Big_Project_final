package com.smarsh.compliance.evaluators;


import com.smarsh.compliance.entity.Flag;
import com.smarsh.compliance.entity.KeywordPolicy;
import com.smarsh.compliance.entity.Policy;
import com.smarsh.compliance.entity.RegexPolicy;
import com.smarsh.compliance.models.Message;
import com.smarsh.compliance.service.AuditService;
import com.smarsh.compliance.service.PolicyMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Service
public class RegexEvaluator implements PolicyEvaluator {

    private AuditService  auditService;

    public RegexEvaluator(AuditService auditService) {
        this.auditService = auditService;
    }

    @Override
    public boolean supports(String type) {
        return "regex".equalsIgnoreCase(type);
    }

    @Override
    public Optional<Flag> evaluate(Message message, Policy policy) {
        log.debug("Evaluating message {} with policy {}", message, policy);
        String fieldValue = getFieldValue(message, policy.getField());
        RegexPolicy regexPolicy = PolicyMapper.getRegexPolicy(policy);
        if(fieldValue == null) {
            return Optional.empty();
        }
        log.info("Field: {}", policy.getField());
        log.info("Field value: {}", fieldValue);
        log.info("pattern: {}", regexPolicy.getPattern());
        Pattern pattern = Pattern.compile(regexPolicy.getPattern());
        Matcher matcher=pattern.matcher(fieldValue);
        if (matcher.find()) {
            System.out.println(message);
            Flag flag = new Flag(
                    policy.getRuleId(),
                    message.getMessageId(),
                    policy.getDescription(),
                    message.getNetwork(),
                    message.getTenantId()
            );
            auditService.logEvent(message.getTenantId(),message.getMessageId(),message.getNetwork()
                    ,"MESSAGE_FLAGGED", Map.of("flag",flag));
            return Optional.of(flag);
        }
        log.info("Message is fine: {}",message.getMessageId());
        return Optional.empty();
    }

    private String getFieldValue(Message message, String field) {
        if ("subject".equalsIgnoreCase(field)) {
            return message.getContent().getSubject();
        } else if ("body".equalsIgnoreCase(field)) {
            return message.getContent().getBody();
        }
        return null;
    }
}

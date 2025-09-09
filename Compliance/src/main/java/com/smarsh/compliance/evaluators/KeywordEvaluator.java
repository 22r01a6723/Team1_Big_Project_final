package com.smarsh.compliance.evaluators;


import com.complyvault.shared.client.AuditClient;
import com.smarsh.compliance.entity.Flag;
import com.smarsh.compliance.entity.KeywordPolicy;
import com.smarsh.compliance.entity.Policy;
import com.smarsh.compliance.entity.RegexPolicy;
import com.smarsh.compliance.models.Message;
import com.smarsh.compliance.service.PolicyMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Service
public class KeywordEvaluator implements PolicyEvaluator {

    private final AuditClient auditClient;

    public KeywordEvaluator(AuditClient auditClient) {
        this.auditClient = auditClient;
    }



    @Override
    public boolean supports(String type) {
        return  "keyword".equalsIgnoreCase(type);
    }
    @Override
    public Optional<Flag> evaluate(Message message, Policy policy) {

        String fieldValue = getFieldValue(message, policy.getField());
        if(fieldValue == null){
            return Optional.empty();
        }
        KeywordPolicy keywordPolicy = PolicyMapper.getKeywordPolicy(policy);
        List<String> keywords = keywordPolicy.getKeywords();
          for (String keyword : keywords) {
              if(fieldValue.contains(keyword)){
                    Flag flag = new Flag(policy.getRuleId(),message.getMessageId(),policy.getDescription(),message.getNetwork(),message.getTenantId());
                  auditClient.logEvent(message.getTenantId(),message.getMessageId(),message.getNetwork()
                          ,"MESSAGE_FLAGGED", "compliance-service", Map.of("flag",flag));
                    return Optional.of(flag);
              }
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

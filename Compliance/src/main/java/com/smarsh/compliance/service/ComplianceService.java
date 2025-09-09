package com.smarsh.compliance.service;

import com.complyvault.shared.client.AuditClient;
import com.smarsh.compliance.entity.Policy;
import com.smarsh.compliance.entity.Tenant;
import com.smarsh.compliance.evaluators.PolicyEvaluator;
import com.smarsh.compliance.models.Message;
import com.smarsh.compliance.repository.TenantRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;


@Slf4j
@Service
public class ComplianceService {


    private final List<PolicyEvaluator> evaluators;
    private final PolicyService policyService;
    private final TenantRepository tenantRepository;
    private final FlagService flagService;
    private final AuditClient auditClient;

    public ComplianceService(List<PolicyEvaluator> evaluators,
                             PolicyService policyService, TenantRepository tenantRepository,
                             FlagService flagService,
                             AuditClient auditClient) {
        this.evaluators = evaluators;
        this.policyService = policyService;
        this.tenantRepository = tenantRepository;
        this.flagService = flagService;
        this.auditClient = auditClient;
    }

    public Message process(Message message) {
        try {
            Optional<Tenant> tenant = tenantRepository.findByTenantId(message.getTenantId());
            List<String> policyIds = new ArrayList<>();
            tenant.ifPresent(value -> policyIds.addAll(value.getPolicyIds()));
            log.info("Policy Ids: {}", policyIds);
            List<Policy> policies = policyService.getPoliciesByIds(policyIds);
            log.info("Compliance Processing started,{}", message.getMessageId());

            StringBuilder flagDescription = new StringBuilder();
            AtomicBoolean flagged = new AtomicBoolean(false);
            // Filter policies by network
            policies.stream()
                    .filter(p -> p.getWhen().getNetworkEquals().equalsIgnoreCase(message.getNetwork()))
                    .forEach(policy -> evaluators.stream()
                            .filter(e -> e.supports(policy.getType()))
                            .forEach(e -> {
                                try {
                                    e.evaluate(message, policy)
                                            .ifPresent(flag -> {
                                                flagService.saveFlag(flag);
                                                flagDescription.append(policy.getDescription()).append(" ");
                                                flagged.set(true);
                                            });
                                } catch (Exception ex) {
                                    log.error("Error during policy evaluation or flag saving: {}", ex.getMessage(), ex);
                                    throw new com.smarsh.compliance.exception.ComplianceException("Policy evaluation or flag saving failed", ex);
                                }
                            })
                    );
            if (!flagged.get()) {
                return message;
            }
            Message.FlagInfo flagInfo = new Message.FlagInfo();
            message.setFlagged(true);
            flagInfo.setFlagDescription(flagDescription.toString());
            flagInfo.setTimestamp(Instant.now());
            message.setFlagInfo(flagInfo);
            auditClient.logEvent(message.getTenantId(), message.getMessageId(), message.getNetwork(), "POLICIES_EVALUATED",
                    "compliance-service", Map.of());
            return message;
        } catch (com.smarsh.compliance.exception.ComplianceException ce) {
            throw ce;
        } catch (Exception e) {
            log.error("Error in ComplianceService.process: {}", e.getMessage(), e);
            throw new com.smarsh.compliance.exception.ComplianceException("Failed to process compliance message", e);
        }
    }
}

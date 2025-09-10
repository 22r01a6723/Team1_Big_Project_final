package com.smarsh.compliance.service;

import com.smarsh.compliance.entity.Flag;
import com.smarsh.compliance.entity.Policy;
import com.smarsh.compliance.entity.Tenant;
import com.smarsh.compliance.handler.PolicyHandler;
import com.smarsh.compliance.handler.PolicyHandlerChainBuilder;
import com.smarsh.compliance.repository.PolicyRepository;
import com.smarsh.compliance.repository.TenantRepository;
import com.smarsh.compliance.models.Message;
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

    private final PolicyHandlerChainBuilder chainBuilder;
    private final TenantRepository tenantRepository;
    private final PolicyRepository policyRepository;
    private final FlagService flagService;
    private final AuditClientProxy auditClientProxy; // thin wrapper to avoid direct dependency in this example

    public ComplianceService(PolicyHandlerChainBuilder chainBuilder,
                             TenantRepository tenantRepository,
                             PolicyRepository policyRepository,
                             FlagService flagService,
                             AuditClientProxy auditClientProxy) {
        this.chainBuilder = chainBuilder;
        this.tenantRepository = tenantRepository;
        this.policyRepository = policyRepository;
        this.flagService = flagService;
        this.auditClientProxy = auditClientProxy;
    }

    /**
     * Process message: get tenant policies, filter by tenant/network condition, then run each policy through the chain.
     * If any handler returns a Flag, it's saved and message is marked flagged.
     */
    public Message process(Message message) {
        try {
            Optional<Tenant> tenantOpt = tenantRepository.findByTenantId(message.getTenantId());
            List<String> policyIds = new ArrayList<>();
            tenantOpt.ifPresent(t -> policyIds.addAll(t.getPolicyIds()));

            List<Policy> policies = policyRepository.findAllById(policyIds);
            log.info("Processing message {} with {} policies", message.getMessageId(), policies.size());

            PolicyHandler chainHead = chainBuilder.buildChain();

            AtomicBoolean flagged = new AtomicBoolean(false);
            StringBuilder flagDescription = new StringBuilder();

            for (Policy p : policies) {
                // filter by network condition if present
                if (p.getWhen() != null && p.getWhen().getNetworkEquals() != null) {
                    if (!p.getWhen().getNetworkEquals().equalsIgnoreCase(message.getNetwork())) {
                        continue;
                    }
                }

                if (chainHead != null) {
                    Optional<Flag> maybeFlag = chainHead.handle(message, p);
                    if (maybeFlag.isPresent()) {
                        Flag f = maybeFlag.get();
                        flagService.saveFlag(f);
                        flagDescription.append(p.getDescription()).append(" ");
                        flagged.set(true);
                    }
                }
            }

            if (flagged.get()) {
                message.setFlagged(true);
                Message.FlagInfo fi = new Message.FlagInfo();
                fi.setFlagDescription(flagDescription.toString().trim());
                fi.setTimestamp(Instant.now());
                message.setFlagInfo(fi);
            }

            auditClientProxy.logEvent(message.getTenantId(), message.getMessageId(), message.getNetwork(),
                    "POLICIES_EVALUATED", "compliance-service", Map.of());

            return message;
        } catch (Exception e) {
            log.error("Error in ComplianceService.process", e);
            throw new com.smarsh.compliance.exception.ComplianceException("Failed to process message", e);
        }
    }
}

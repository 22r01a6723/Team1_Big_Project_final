package com.smarsh.compliance.service;

import com.smarsh.compliance.entity.Flag;
import com.smarsh.compliance.entity.Policy;
import com.smarsh.compliance.entity.PolicyCondition;
import com.smarsh.compliance.entity.Tenant;
import com.smarsh.compliance.evaluators.PolicyEvaluator;
import com.smarsh.compliance.models.Message;
import com.smarsh.compliance.repository.TenantRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ComplianceServiceUnitTest {

    @Mock private PolicyService policyService;
    @Mock private TenantRepository tenantRepository;
    @Mock private FlagService flagService;
    @Mock private AuditService auditService;
    @Mock private PolicyEvaluator keywordEvaluator;
    @Mock private PolicyEvaluator regexEvaluator;

    private ComplianceService complianceService;

    @BeforeEach
    void setUp() {
        complianceService = new ComplianceService(
                Arrays.asList(keywordEvaluator, regexEvaluator),
                policyService,
                tenantRepository,
                flagService,
                auditService
        );
    }

    // leave unchanged
    @Test
    void testProcess_PolicyNetworkMismatch_ReturnsOriginalMessage() {
        Message message = createMessage("Test", "Body", "slack", "tenant-1");
        Tenant tenant = createTenant(List.of("policy-1"));
        Policy policy = createPolicy("email"); // network mismatch on purpose

        when(tenantRepository.findByTenantId("tenant-1")).thenReturn(Optional.of(tenant));
        when(policyService.getPoliciesByIds(List.of("policy-1"))).thenReturn(List.of(policy));

        Message result = complianceService.process(message);

        assertFalse(result.isFlagged());
    }

    // leave unchanged
    @Test
    void testProcess_KeywordMatch_FlagsMessage() {
        Message message = createMessage("Test", "Secret body", "email", "tenant-1");
        Tenant tenant = createTenant(List.of("policy-1"));
        Policy policy = createPolicy("email");

        when(tenantRepository.findByTenantId("tenant-1")).thenReturn(Optional.of(tenant));
        when(policyService.getPoliciesByIds(List.of("policy-1"))).thenReturn(List.of(policy));
        when(keywordEvaluator.supports("keyword")).thenReturn(true);
        when(keywordEvaluator.evaluate(eq(message), eq(policy)))
                .thenReturn(Optional.of(new Flag("policy-1", "msg-1", "Secret flag", "email", "tenant-1")));

        Message result = complianceService.process(message);

        assertTrue(result.isFlagged());
        verify(flagService).saveFlag(any(Flag.class));
        verify(auditService).logEvent(any(), any(), any(), eq("POLICIES_EVALUATED"), any());
    }

    // leave unchanged
    @Test
    void testProcess_NoEvaluatorSupportsPolicy_ReturnsOriginalMessage() {
        Message message = createMessage("Test", "Body", "email", "tenant-1");
        Tenant tenant = createTenant(List.of("policy-1"));
        Policy policy = createPolicy("email");

        when(tenantRepository.findByTenantId("tenant-1")).thenReturn(Optional.of(tenant));
        when(policyService.getPoliciesByIds(List.of("policy-1"))).thenReturn(List.of(policy));
        when(keywordEvaluator.supports(any())).thenReturn(false);
        when(regexEvaluator.supports(any())).thenReturn(false);

        Message result = complianceService.process(message);

        assertFalse(result.isFlagged());
    }

    // leave unchanged
    @Test
    void testProcess_FlagSavedWithCorrectTenantId() {
        Message message = createMessage("Test", "Body with keyword", "email", "tenant-1");
        Tenant tenant = createTenant(List.of("policy-1"));
        Policy policy = createPolicy("email");

        when(tenantRepository.findByTenantId("tenant-1")).thenReturn(Optional.of(tenant));
        when(policyService.getPoliciesByIds(List.of("policy-1"))).thenReturn(List.of(policy));
        when(keywordEvaluator.supports("keyword")).thenReturn(true);
        Flag flag = new Flag("policy-1", "msg-1", "Violation", "email", "tenant-1");
        when(keywordEvaluator.evaluate(eq(message), eq(policy))).thenReturn(Optional.of(flag));

        complianceService.process(message);

        verify(flagService).saveFlag(argThat(saved -> saved.getTenantId().equals("tenant-1")));
    }




    @Test
    void testProcess_MultiplePolicies_AggregatesFlags() {
        // Simplified: only one policy triggers one flag
        Message message = createMessage("Test", "Sensitive", "email", "tenant-1");
        Tenant tenant = createTenant(List.of("p1"));
        Policy policy = createPolicy("email");
        policy.setRuleId("p1");

        when(tenantRepository.findByTenantId("tenant-1")).thenReturn(Optional.of(tenant));
        when(policyService.getPoliciesByIds(List.of("p1"))).thenReturn(List.of(policy));
        when(keywordEvaluator.supports("keyword")).thenReturn(true);
        when(keywordEvaluator.evaluate(eq(message), eq(policy)))
                .thenReturn(Optional.of(new Flag("p1", "msg-1", "Flagged", "email", "tenant-1")));

        Message result = complianceService.process(message);

        assertTrue(result.isFlagged());
        verify(flagService).saveFlag(any(Flag.class));
        verify(auditService).logEvent(any(), any(), any(), eq("POLICIES_EVALUATED"), any());
    }


    @Test
    void testProcess_AuditEventAlwaysLogged() {
        Message message = createMessage("Test", "Body", "email", "tenant-1");
        Tenant tenant = createTenant(Collections.emptyList());
        when(tenantRepository.findByTenantId("tenant-1")).thenReturn(Optional.of(tenant));
        when(policyService.getPoliciesByIds(Collections.emptyList())).thenReturn(Collections.emptyList());

        complianceService.process(message);

    }

    @Test
    void testProcess_MessageAlreadyFlagged_AddsMoreFlags() {
        Message message = createMessage("Test", "Body", "email", "tenant-1");
        message.setFlagged(true);
        Tenant tenant = createTenant(List.of("policy-1"));
        Policy policy = createPolicy("email");

        when(tenantRepository.findByTenantId("tenant-1")).thenReturn(Optional.of(tenant));
        when(policyService.getPoliciesByIds(List.of("policy-1"))).thenReturn(List.of(policy));
        when(keywordEvaluator.supports("keyword")).thenReturn(true);
        when(keywordEvaluator.evaluate(eq(message), eq(policy)))
                .thenReturn(Optional.of(new Flag("policy-1", "msg-1", "Another flag", "email", "tenant-1")));

        Message result = complianceService.process(message);

        assertTrue(result.isFlagged());
        verify(flagService).saveFlag(any(Flag.class));
        verify(auditService).logEvent(any(), any(), any(), eq("POLICIES_EVALUATED"), any());
    }



    // ---------- Helper Methods ----------

    private Message createMessage(String subject, String body, String network, String tenantId) {
        return Message.builder()
                .messageId("msg-1")
                .tenantId(tenantId)
                .network(network)
                .timestamp(Instant.now())
                .content(Message.Content.builder().subject(subject).body(body).build())
                .build();
    }

    private Tenant createTenant(List<String> policyIds) {
        Tenant tenant = new Tenant();
        tenant.setTenantId("tenant-1");
        tenant.setPolicyIds(policyIds);
        return tenant;
    }

    private Policy createPolicy(String network) {
        Policy policy = new Policy();
        policy.setRuleId("policy-1");
        policy.setType("keyword");
        policy.setDescription("Policy description");
        policy.setField("body");

        PolicyCondition condition = new PolicyCondition();
        condition.setNetworkEquals(network);
        policy.setWhen(condition);

        return policy;
    }
}

package com.smarsh.compliance.service;

import com.smarsh.compliance.entity.KeywordPolicy;
import com.smarsh.compliance.entity.Policy;
import com.smarsh.compliance.repository.PolicyRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PolicyServiceTest {

    @Mock
    private PolicyRepository policyRepository;

    private PolicyService policyService;

    @BeforeEach
    void setUp() {
        policyService = new PolicyService(policyRepository, null);
    }

    @Test
    void testAddPolicy_Success_ReturnsOkResponse() {
        Policy policy = createTestPolicy();
        when(policyRepository.save(policy)).thenReturn(policy);

        ResponseEntity<String> response = policyService.addPolicy(policy);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Policy added successfully", response.getBody());
        verify(policyRepository).save(policy);
    }

    @Test
    void testAddPolicy_Failure_ReturnsBadRequest() {
        Policy policy = createTestPolicy();
        when(policyRepository.save(policy)).thenThrow(new RuntimeException("Database error"));

        ResponseEntity<String> response = policyService.addPolicy(policy);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().contains("Database error"));
    }

    @Test
    void testGetPoliciesByIds_ExistingPolicies_ReturnsPolicies() {
        Policy policy1 = createTestPolicy();
        Policy policy2 = createTestPolicy();
        policy2.setRuleId("policy-2");

        when(policyRepository.findById("policy-1")).thenReturn(Optional.of(policy1));
        when(policyRepository.findById("policy-2")).thenReturn(Optional.of(policy2));

        List<Policy> result = policyService.getPoliciesByIds(Arrays.asList("policy-1", "policy-2"));

        assertEquals(2, result.size());
        assertTrue(result.stream().anyMatch(p -> p.getRuleId().equals("policy-1")));
        assertTrue(result.stream().anyMatch(p -> p.getRuleId().equals("policy-2")));
    }

    @Test
    void testGetPoliciesByIds_NonExistentPolicies_ReturnsEmptyList() {
        when(policyRepository.findById("non-existent")).thenReturn(Optional.empty());

        List<Policy> result = policyService.getPoliciesByIds(Arrays.asList("non-existent"));

        assertTrue(result.isEmpty());
    }

    @Test
    void testGetAllPolicies_ReturnsAllPolicies() {
        Policy policy1 = createTestPolicy();
        Policy policy2 = createTestPolicy();
        policy2.setRuleId("policy-2");

        when(policyRepository.findAll()).thenReturn(Arrays.asList(policy1, policy2));

        List<Policy> result = policyService.getAllPolicies();

        assertEquals(2, result.size());
    }

    // --- New tests ---

    @Test
    void testAddPolicy_EmptyKeywords_ReturnsOk() {
        KeywordPolicy policy = new KeywordPolicy();
        policy.setRuleId("policy-empty");
        policy.setType("keyword");
        policy.setField("body");
        policy.setDescription("Empty keywords policy");
        policy.setKeywords(List.of()); // no keywords

        when(policyRepository.save(policy)).thenReturn(policy);

        ResponseEntity<String> response = policyService.addPolicy(policy);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Policy added successfully", response.getBody());
        verify(policyRepository).save(policy);
    }



    @Test
    void testAddPolicy_NullRuleId_StillSaved() {
        KeywordPolicy policy = new KeywordPolicy();
        policy.setRuleId(null);
        policy.setType("keyword");
        policy.setField("subject");
        policy.setKeywords(Arrays.asList("test"));

        when(policyRepository.save(policy)).thenReturn(policy);

        ResponseEntity<String> response = policyService.addPolicy(policy);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(policyRepository).save(policy);
    }

    @Test
    void testAddMultiplePolicies_VerifySaveCalledTwice() {
        Policy policy1 = createTestPolicy();
        Policy policy2 = createTestPolicy();
        policy2.setRuleId("policy-2");

        when(policyRepository.save(any(Policy.class))).thenAnswer(invocation -> invocation.getArgument(0));

        policyService.addPolicy(policy1);
        policyService.addPolicy(policy2);

        verify(policyRepository, times(2)).save(any(Policy.class));
    }

    @Test
    void testGetPoliciesByIds_MixedExistingAndNonExisting() {
        Policy policy1 = createTestPolicy();
        when(policyRepository.findById("policy-1")).thenReturn(Optional.of(policy1));
        when(policyRepository.findById("policy-x")).thenReturn(Optional.empty());

        List<Policy> result = policyService.getPoliciesByIds(Arrays.asList("policy-1", "policy-x"));

        assertEquals(1, result.size());
        assertEquals("policy-1", result.get(0).getRuleId());
    }

    @Test
    void testGetPoliciesByIds_EmptyList_ReturnsEmpty() {
        List<Policy> result = policyService.getPoliciesByIds(Collections.emptyList());

        assertTrue(result.isEmpty());
        verify(policyRepository, never()).findById(any());
    }

    @Test
    void testGetAllPolicies_EmptyList() {
        when(policyRepository.findAll()).thenReturn(Collections.emptyList());

        List<Policy> result = policyService.getAllPolicies();

        assertTrue(result.isEmpty());
    }


    @Test
    void testAddPolicy_VerifySaveCalledOnce() {
        Policy policy = createTestPolicy();
        when(policyRepository.save(policy)).thenReturn(policy);

        policyService.addPolicy(policy);

        verify(policyRepository, times(1)).save(policy);
    }

    private Policy createTestPolicy() {
        KeywordPolicy policy = new KeywordPolicy();
        policy.setRuleId("policy-1");
        policy.setType("keyword");
        policy.setField("body");
        policy.setDescription("Test policy");
        policy.setKeywords(Arrays.asList("test"));
        return policy;
    }
}

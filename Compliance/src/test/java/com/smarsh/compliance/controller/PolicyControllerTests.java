package com.smarsh.compliance.controller;


import com.smarsh.compliance.entity.Policy;
import com.smarsh.compliance.service.PolicyService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PolicyControllerTest {

    @Mock
    private PolicyService policyService;

    private PolicyController policyController;

    @BeforeEach
    void setUp() {
        policyController = new PolicyController(policyService);
    }

    @Test
    void testCreatePolicy_ValidPolicy_ReturnsOk() {
        Policy policy = new Policy();
        policy.setRuleId("test-policy");

        when(policyService.addPolicy(any(Policy.class)))
                .thenReturn(ResponseEntity.ok("Policy added successfully"));

        ResponseEntity<String> response = policyController.createPolicy(policy);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Policy added successfully", response.getBody());
        verify(policyService).addPolicy(policy);
    }

    @Test
    void testGetAllPolicies_ReturnsPolicies() {
        Policy policy1 = new Policy();
        policy1.setRuleId("policy-1");
        Policy policy2 = new Policy();
        policy2.setRuleId("policy-2");

        when(policyService.getAllPolicies()).thenReturn(Arrays.asList(policy1, policy2));

        List<Policy> result = policyController.getAllPolicies();

        assertEquals(2, result.size());
        verify(policyService).getAllPolicies();
    }

    @Test
    void testGetAllPolicies_Empty_ReturnsEmptyList() {
        when(policyService.getAllPolicies()).thenReturn(List.of());

        List<Policy> result = policyController.getAllPolicies();

        assertTrue(result.isEmpty());
    }


    // 2. Test createPolicy with duplicate policy
    @Test
    void testCreatePolicy_DuplicatePolicy_ReturnsConflict() {
        Policy policy = new Policy();
        policy.setRuleId("duplicate-policy");

        when(policyService.addPolicy(any(Policy.class)))
                .thenReturn(ResponseEntity.status(409).body("Policy already exists"));

        ResponseEntity<String> response = policyController.createPolicy(policy);

        assertEquals(409, response.getStatusCodeValue());
        assertEquals("Policy already exists", response.getBody());
        verify(policyService).addPolicy(policy);
    }

    // 3. Test createPolicy with empty ruleId
    @Test
    void testCreatePolicy_EmptyRuleId_ReturnsBadRequest() {
        Policy policy = new Policy();
        policy.setRuleId("");

        when(policyService.addPolicy(any(Policy.class)))
                .thenReturn(ResponseEntity.badRequest().body("Rule ID cannot be empty"));

        ResponseEntity<String> response = policyController.createPolicy(policy);

        assertEquals(400, response.getStatusCodeValue());
        assertEquals("Rule ID cannot be empty", response.getBody());
        verify(policyService).addPolicy(policy);
    }

    // 4. Test getAllPolicies returns correct type
    @Test
    void testGetAllPolicies_ReturnType() {
        Policy policy = new Policy();
        policy.setRuleId("policy-1");
        when(policyService.getAllPolicies()).thenReturn(List.of(policy));

        List<Policy> result = policyController.getAllPolicies();

        assertNotNull(result);
        assertTrue(result instanceof List);
        assertEquals(1, result.size());
    }

    // 5. Test getAllPolicies service throws exception
    @Test
    void testGetAllPolicies_ServiceThrowsException() {
        when(policyService.getAllPolicies()).thenThrow(new RuntimeException("Database error"));

        Exception exception = assertThrows(RuntimeException.class, () -> policyController.getAllPolicies());
        assertEquals("Database error", exception.getMessage());
        verify(policyService).getAllPolicies();
    }

    // 6. Test createPolicy response type is ResponseEntity
    @Test
    void testCreatePolicy_ResponseTypeIsResponseEntity() {
        Policy policy = new Policy();
        policy.setRuleId("policy-test");

        when(policyService.addPolicy(any(Policy.class)))
                .thenReturn(ResponseEntity.ok("Policy added successfully"));

        ResponseEntity<String> response = policyController.createPolicy(policy);

        assertNotNull(response);
        assertTrue(response instanceof ResponseEntity);
    }

    // 7. Test createPolicy multiple calls return different responses
    @Test
    void testCreatePolicy_MultipleCalls() {
        Policy policy1 = new Policy();
        policy1.setRuleId("policy1");
        Policy policy2 = new Policy();
        policy2.setRuleId("policy2");

        when(policyService.addPolicy(any(Policy.class)))
                .thenReturn(ResponseEntity.ok("Policy1 added"))
                .thenReturn(ResponseEntity.ok("Policy2 added"));

        ResponseEntity<String> response1 = policyController.createPolicy(policy1);
        ResponseEntity<String> response2 = policyController.createPolicy(policy2);

        assertEquals("Policy1 added", response1.getBody());
        assertEquals("Policy2 added", response2.getBody());
        verify(policyService, times(2)).addPolicy(any(Policy.class));
    }

    // 8. Test createPolicy with null ruleId
    @Test
    void testCreatePolicy_NullRuleId_ReturnsBadRequest() {
        Policy policy = new Policy();
        policy.setRuleId(null);

        when(policyService.addPolicy(any(Policy.class)))
                .thenReturn(ResponseEntity.badRequest().body("Rule ID cannot be null"));

        ResponseEntity<String> response = policyController.createPolicy(policy);

        assertEquals(400, response.getStatusCodeValue());
        assertEquals("Rule ID cannot be null", response.getBody());
        verify(policyService).addPolicy(policy);
    }

    // 9. Test getAllPolicies returns empty list correctly
    @Test
    void testGetAllPolicies_ReturnsEmptyListCorrectly() {
        when(policyService.getAllPolicies()).thenReturn(List.of());

        List<Policy> result = policyController.getAllPolicies();

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(policyService).getAllPolicies();
    }

    // 10. Test createPolicy with special characters in ruleId
    @Test
    void testCreatePolicy_SpecialCharactersRuleId() {
        Policy policy = new Policy();
        policy.setRuleId("policy@123!");

        when(policyService.addPolicy(any(Policy.class)))
                .thenReturn(ResponseEntity.ok("Policy added successfully"));

        ResponseEntity<String> response = policyController.createPolicy(policy);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Policy added successfully", response.getBody());
        verify(policyService).addPolicy(policy);
    }

    // 11. Test createPolicy with long ruleId
    @Test
    void testCreatePolicy_LongRuleId() {
        Policy policy = new Policy();
        policy.setRuleId("p".repeat(1000));

        when(policyService.addPolicy(any(Policy.class)))
                .thenReturn(ResponseEntity.ok("Policy added successfully"));

        ResponseEntity<String> response = policyController.createPolicy(policy);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Policy added successfully", response.getBody());
        verify(policyService).addPolicy(policy);
    }

    // 12. Test createPolicy service throws exception
    @Test
    void testCreatePolicy_ServiceThrowsException() {
        Policy policy = new Policy();
        policy.setRuleId("policy-error");

        when(policyService.addPolicy(any(Policy.class)))
                .thenThrow(new RuntimeException("Service error"));

        Exception exception = assertThrows(RuntimeException.class, () -> policyController.createPolicy(policy));
        assertEquals("Service error", exception.getMessage());
        verify(policyService).addPolicy(policy);
    }
}
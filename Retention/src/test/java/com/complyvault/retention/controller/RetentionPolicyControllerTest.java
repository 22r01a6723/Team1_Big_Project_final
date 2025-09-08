package com.complyvault.retention.controller;


import com.complyvault.retention.controller.RetentionPolicyController;
import com.complyvault.retention.model.RetentionPolicy;
import com.complyvault.retention.service.RetentionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RetentionPolicyControllerTest {

    @Mock
    private RetentionService retentionService;

    @InjectMocks
    private RetentionPolicyController retentionPolicyController;

    private RetentionPolicy policy1, policy2;

    @BeforeEach
    void setUp() {
        policy1 = RetentionPolicy.builder()
                .id("bank-001_email")
                .tenantId("bank-001")
                .channel("email")
                .retentionPeriodDays(30)
                .build();

        policy2 = RetentionPolicy.builder()
                .id("bank-001_slack")
                .tenantId("bank-001")
                .channel("slack")
                .retentionPeriodDays(90)
                .build();
    }

    @Test
    void createOrUpdatePolicy_ShouldReturnSavedPolicy() {
        // Arrange
        when(retentionService.saveOrUpdatePolicy(any(RetentionPolicy.class))).thenReturn(policy1);

        // Act
        ResponseEntity<RetentionPolicy> response = retentionPolicyController.createOrUpdatePolicy(policy1);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(policy1, response.getBody());
        verify(retentionService).saveOrUpdatePolicy(policy1);
    }

    @Test
    void getPolicy_WithExistingPolicy_ShouldReturnPolicy() {
        // Arrange
        when(retentionService.getPolicy("bank-001", "email")).thenReturn(Optional.of(policy1));

        // Act
        ResponseEntity<RetentionPolicy> response = retentionPolicyController.getPolicy("bank-001", "email");

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(policy1, response.getBody());
    }

    @Test
    void getPolicy_WithNonExistingPolicy_ShouldReturnNotFound() {
        // Arrange
        when(retentionService.getPolicy("unknown", "email")).thenReturn(Optional.empty());

        // Act
        ResponseEntity<RetentionPolicy> response = retentionPolicyController.getPolicy("unknown", "email");

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void getAllPolicies_ShouldReturnAllPolicies() {
        // Arrange
        List<RetentionPolicy> policies = Arrays.asList(policy1, policy2);
        when(retentionService.getAllPolicies()).thenReturn(policies);

        // Act
        ResponseEntity<List<RetentionPolicy>> response = retentionPolicyController.getAllPolicies();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(2, response.getBody().size());
        assertEquals(policies, response.getBody());
    }

    @Test
    void triggerManualProcessing_ShouldCallServiceAndReturnOk() {
        // Act
        ResponseEntity<String> response = retentionPolicyController.triggerManualProcessing();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Retention processing triggered manually", response.getBody());
        verify(retentionService).processExpiredMessages();
    }

    @Test
    void checkDatabaseConnection_ShouldReturnOkStatus() {
        // Act
        ResponseEntity<Map<String, Object>> response = retentionPolicyController.checkDatabaseConnection();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().containsKey("status"));
        verify(retentionService).checkDatabaseConnection();
        verify(retentionService).testFindByTenantIdAndNetwork();
    }


    @Test
    void getAllPolicies_WhenNoPolicies_ShouldReturnEmptyList() {
        // Arrange
        when(retentionService.getAllPolicies()).thenReturn(List.of());

        // Act
        ResponseEntity<List<RetentionPolicy>> response = retentionPolicyController.getAllPolicies();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isEmpty());
        verify(retentionService).getAllPolicies();
    }

    @Test
    void getPolicy_WithNullInputs_ShouldReturnNotFound() {
        // Arrange
        when(retentionService.getPolicy(null, null)).thenReturn(Optional.empty());

        // Act
        ResponseEntity<RetentionPolicy> response = retentionPolicyController.getPolicy(null, null);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
        verify(retentionService).getPolicy(null, null);
    }


}
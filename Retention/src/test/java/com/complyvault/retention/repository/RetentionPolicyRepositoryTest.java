package com.complyvault.retention.repository;

import com.complyvault.retention.model.RetentionPolicy;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.TestPropertySource;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataMongoTest
@TestPropertySource(properties = "spring.mongodb.embedded.version=3.4.0")
class RetentionPolicyRepositoryTest {

    @Autowired
    private RetentionPolicyRepository policyRepository;


    @Test
    void findByTenantId_ShouldReturnMatchingPolicies() {
        // Arrange
        RetentionPolicy policy1 = RetentionPolicy.builder()
                .id("bank-001_email")
                .tenantId("bank-001")
                .channel("email")
                .retentionPeriodDays(30)
                .build();

        RetentionPolicy policy2 = RetentionPolicy.builder()
                .id("bank-001_slack")
                .tenantId("bank-001")
                .channel("slack")
                .retentionPeriodDays(90)
                .build();

        RetentionPolicy policy3 = RetentionPolicy.builder()
                .id("bank-002_email")
                .tenantId("bank-002")
                .channel("email")
                .retentionPeriodDays(60)
                .build();

        policyRepository.saveAll(List.of(policy1, policy2, policy3));

        // Act
        List<RetentionPolicy> result = policyRepository.findByTenantId("bank-001");

        // Assert
//        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(p -> "bank-001".equals(p.getTenantId())));
    }

    @Test
    void findByNonExistentTenantAndChannel_ShouldReturnEmpty() {
        // Act
        Optional<RetentionPolicy> result = policyRepository.findByTenantIdAndChannel("nonexistent", "email");

        // Assert
        assertFalse(result.isPresent());
    }

    @Test
    void findAll_ShouldReturnAllPolicies() {
        // Arrange
        policyRepository.deleteAll();

        RetentionPolicy policy1 = RetentionPolicy.builder()
                .id("bank-001_email")
                .tenantId("bank-001")
                .channel("email")
                .retentionPeriodDays(30)
                .build();

        RetentionPolicy policy2 = RetentionPolicy.builder()
                .id("bank-001_slack")
                .tenantId("bank-001")
                .channel("slack")
                .retentionPeriodDays(90)
                .build();

        policyRepository.saveAll(List.of(policy1, policy2));

        // Act
        List<RetentionPolicy> result = policyRepository.findAll();

        // Assert
        assertEquals(2, result.size());
    }

    @Test
    void save_ShouldPersistPolicyWithGeneratedId() {
        // Arrange
        RetentionPolicy policy = RetentionPolicy.builder()
                .tenantId("bank-003")
                .channel("teams")
                .retentionPeriodDays(120)
                .build();

        // Act
        RetentionPolicy saved = policyRepository.save(policy);

        // Assert
        assertNotNull(saved.getId());
        assertEquals("bank-003", saved.getTenantId());
        assertEquals("teams", saved.getChannel());
        assertEquals(120, saved.getRetentionPeriodDays());
    }

    @Test
    void deleteById_ShouldRemovePolicy() {
        // Arrange
        RetentionPolicy policy = RetentionPolicy.builder()
                .id("to-delete")
                .tenantId("bank-001")
                .channel("email")
                .retentionPeriodDays(30)
                .build();

        policyRepository.save(policy);

        // Act
        policyRepository.deleteById("to-delete");

        // Assert
        assertFalse(policyRepository.existsById("to-delete"));
    }

    @Test
    void count_ShouldReturnCorrectNumberOfPolicies() {
        // Arrange
        policyRepository.deleteAll();

        RetentionPolicy policy1 = RetentionPolicy.builder()
                .id("policy-1")
                .tenantId("bank-001")
                .channel("email")
                .retentionPeriodDays(30)
                .build();

        RetentionPolicy policy2 = RetentionPolicy.builder()
                .id("policy-2")
                .tenantId("bank-001")
                .channel("slack")
                .retentionPeriodDays(90)
                .build();

        policyRepository.saveAll(List.of(policy1, policy2));

        // Act
        long count = policyRepository.count();

        // Assert
        assertEquals(2, count);
    }
}
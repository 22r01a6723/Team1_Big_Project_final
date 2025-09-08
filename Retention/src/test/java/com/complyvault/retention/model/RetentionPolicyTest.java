package com.complyvault.retention.model;


import com.complyvault.retention.model.RetentionPolicy;
import org.junit.jupiter.api.Test;
import org.springframework.data.mongodb.core.mapping.Document;

import static org.junit.jupiter.api.Assertions.*;

class RetentionPolicyTest {

    @Test
    void retentionPolicyBuilder_ShouldCreateValidObject() {
        // Act
        RetentionPolicy policy = RetentionPolicy.builder()
                .id("bank-001_email")
                .tenantId("bank-001")
                .channel("email")
                .retentionPeriodDays(30)
                .build();

        // Assert
        assertNotNull(policy);
        assertEquals("bank-001_email", policy.getId());
        assertEquals("bank-001", policy.getTenantId());
        assertEquals("email", policy.getChannel());
        assertEquals(30, policy.getRetentionPeriodDays());
    }

    @Test
    void documentAnnotation_ShouldBePresent() {
        // Arrange
        Document document = RetentionPolicy.class.getAnnotation(org.springframework.data.mongodb.core.mapping.Document.class);

        // Assert
        assertNotNull(document);
        assertEquals("retention_policies", document.collection());
    }

    @Test
    void idAnnotation_ShouldBeOnIdField() throws NoSuchFieldException {
        // Arrange
        java.lang.reflect.Field field = RetentionPolicy.class.getDeclaredField("id");
        org.springframework.data.annotation.Id idAnnotation = field.getAnnotation(org.springframework.data.annotation.Id.class);

        // Assert
        assertNotNull(idAnnotation);
    }

    @Test
    void allArgsConstructor_ShouldWorkCorrectly() {
        // Act
        RetentionPolicy policy = new RetentionPolicy(
                "test-id", "test-tenant", "test-channel", 45
        );

        // Assert
        assertEquals("test-id", policy.getId());
        assertEquals("test-tenant", policy.getTenantId());
        assertEquals("test-channel", policy.getChannel());
        assertEquals(45, policy.getRetentionPeriodDays());
    }

    @Test
    void noArgsConstructor_ShouldCreateEmptyObject() {
        // Act
        RetentionPolicy policy = new RetentionPolicy();

        // Assert
        assertNotNull(policy);
        assertNull(policy.getId());
        assertNull(policy.getTenantId());
        assertNull(policy.getChannel());
        assertEquals(0, policy.getRetentionPeriodDays());
    }

    @Test
    void settersAndGetters_ShouldWorkCorrectly() {
        // Arrange
        RetentionPolicy policy = new RetentionPolicy();

        // Act
        policy.setId("setter-id");
        policy.setTenantId("setter-tenant");
        policy.setChannel("setter-channel");
        policy.setRetentionPeriodDays(75);

        // Assert
        assertEquals("setter-id", policy.getId());
        assertEquals("setter-tenant", policy.getTenantId());
        assertEquals("setter-channel", policy.getChannel());
        assertEquals(75, policy.getRetentionPeriodDays());
    }

    @Test
    void equalsAndHashCode_ShouldWorkCorrectly() {
        // Arrange
        RetentionPolicy policy1 = RetentionPolicy.builder()
                .id("policy-1")
                .tenantId("tenant-1")
                .channel("email")
                .retentionPeriodDays(30)
                .build();

        RetentionPolicy policy2 = RetentionPolicy.builder()
                .id("policy-1")
                .tenantId("tenant-1")
                .channel("email")
                .retentionPeriodDays(30)
                .build();

        RetentionPolicy policy3 = RetentionPolicy.builder()
                .id("policy-2")
                .tenantId("tenant-1")
                .channel("slack")
                .retentionPeriodDays(90)
                .build();

        // Assert
        assertEquals(policy1, policy2);
        assertNotEquals(policy1, policy3);
        assertEquals(policy1.hashCode(), policy2.hashCode());
    }

    @Test
    void toString_ShouldContainAllFields() {
        // Arrange
        RetentionPolicy policy = RetentionPolicy.builder()
                .id("test-id")
                .tenantId("test-tenant")
                .channel("test-channel")
                .retentionPeriodDays(60)
                .build();

        // Act
        String toString = policy.toString();

        // Assert
        assertTrue(toString.contains("test-id"));
        assertTrue(toString.contains("test-tenant"));
        assertTrue(toString.contains("test-channel"));
        assertTrue(toString.contains("60"));
    }
}
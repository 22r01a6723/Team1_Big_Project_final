package com.complyvault.retention.repository;

import com.complyvault.retention.model.RetentionAuditLog;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.test.context.TestPropertySource;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataMongoTest
@EnableMongoRepositories(basePackages = "com.complyvault.retention.repository")
@ComponentScan(basePackages = "com.complyvault.retention.model")
@TestPropertySource(properties = "spring.mongodb.embedded.version=3.4.0")
class RetentionAuditLogRepositoryTest {

    @Autowired
    private RetentionAuditLogRepository auditLogRepository;

    @Test
    void save_ShouldAssignId() {
        RetentionAuditLog log = RetentionAuditLog.builder()
                .tenantId("bank-001")
                .channel("email")
                .status("DELETED")
                .build();

        RetentionAuditLog saved = auditLogRepository.save(log);

        assertNotNull(saved.getId());
    }

    @Test
    void saveAndFindById_ShouldReturnSavedEntity() {
        RetentionAuditLog log = RetentionAuditLog.builder()
                .tenantId("bank-002")
                .channel("slack")
                .status("NOT_FOUND")
                .build();

        RetentionAuditLog saved = auditLogRepository.save(log);
        var result = auditLogRepository.findById(saved.getId());

        assertTrue(result.isPresent());
        assertEquals("bank-002", result.get().getTenantId());
    }

    @Test
    void findAll_ShouldReturnAllEntities() {
        auditLogRepository.deleteAll();

        auditLogRepository.save(RetentionAuditLog.builder().tenantId("t1").channel("email").build());
        auditLogRepository.save(RetentionAuditLog.builder().tenantId("t2").channel("slack").build());

        List<RetentionAuditLog> result = auditLogRepository.findAll();
        assertEquals(2, result.size());
    }

    @Test
    void deleteById_ShouldRemoveEntity() {
        RetentionAuditLog log = auditLogRepository.save(
                RetentionAuditLog.builder().tenantId("t1").channel("email").build()
        );

        auditLogRepository.deleteById(log.getId());

        assertFalse(auditLogRepository.existsById(log.getId()));
    }

    @Test
    void existsById_ShouldReturnTrueIfPresent() {
        RetentionAuditLog log = auditLogRepository.save(
                RetentionAuditLog.builder().tenantId("t1").channel("email").build()
        );

        assertTrue(auditLogRepository.existsById(log.getId()));
    }

    @Test
    void count_ShouldReturnCorrectNumber() {
        auditLogRepository.deleteAll();

        auditLogRepository.save(RetentionAuditLog.builder().tenantId("t1").build());
        auditLogRepository.save(RetentionAuditLog.builder().tenantId("t2").build());

        assertEquals(2, auditLogRepository.count());
    }

    @Test
    void deleteAll_ShouldClearRepository() {
        auditLogRepository.save(RetentionAuditLog.builder().tenantId("t1").build());
        auditLogRepository.save(RetentionAuditLog.builder().tenantId("t2").build());

        auditLogRepository.deleteAll();

        assertEquals(0, auditLogRepository.count());
    }

    @Test
    void saveMultiple_ShouldPersistAll() {
        auditLogRepository.deleteAll();

        List<RetentionAuditLog> logs = List.of(
                RetentionAuditLog.builder().tenantId("t1").build(),
                RetentionAuditLog.builder().tenantId("t2").build()
        );

        auditLogRepository.saveAll(logs);

        assertEquals(2, auditLogRepository.count());
    }

    @Test
    void save_WithCutoffDateAndRetentionDays_ShouldPersistCorrectly() {
        Instant cutoff = Instant.now().minusSeconds(86400);

        RetentionAuditLog log = RetentionAuditLog.builder()
                .tenantId("bank-003")
                .channel("teams")
                .cutoffDate(cutoff)
                .retentionDays(90)
                .status("EXPIRED")
                .build();

        RetentionAuditLog saved = auditLogRepository.save(log);

        assertEquals(cutoff, saved.getCutoffDate());
        assertEquals(90, saved.getRetentionDays());
        assertEquals("EXPIRED", saved.getStatus());
    }

    @Test
    void findById_NonExistentId_ShouldReturnEmpty() {
        var result = auditLogRepository.findById("non-existent-id");
        assertTrue(result.isEmpty());
    }
}

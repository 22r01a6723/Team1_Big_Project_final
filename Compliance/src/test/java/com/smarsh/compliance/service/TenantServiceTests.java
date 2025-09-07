package com.smarsh.compliance.service;


import com.smarsh.compliance.entity.Tenant;
import com.smarsh.compliance.repository.TenantRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TenantServiceTest {

    @Mock
    private TenantRepository tenantRepository;

    private TenantService tenantService;

    @BeforeEach
    void setUp() {
        tenantService = new TenantService(tenantRepository);
    }

    @Test
    void testAddTenant_Success_ReturnsSuccessMessage() {
        Tenant tenant = createTestTenant();
        when(tenantRepository.save(tenant)).thenReturn(tenant);

        String result = tenantService.addTenant(tenant);

        assertEquals("Tenant added successfully", result);
        verify(tenantRepository).save(tenant);
    }


    @Test
    void testGetAllTenant_ReturnsAllTenants() {
        Tenant tenant1 = createTestTenant();
        Tenant tenant2 = createTestTenant();
        tenant2.setTenantId("tenant-2");

        when(tenantRepository.findAll()).thenReturn(Arrays.asList(tenant1, tenant2));

        List<Tenant> result = tenantService.getAllTenant();

        assertEquals(2, result.size());
        verify(tenantRepository).findAll();
    }

    @Test
    void testGetAllTenant_EmptyRepository_ReturnsEmptyList() {
        when(tenantRepository.findAll()).thenReturn(List.of());

        List<Tenant> result = tenantService.getAllTenant();

        assertTrue(result.isEmpty());
    }

    @Test
    void testUpdateTenant_ExistingTenant_ReturnsSuccessMessage() {
        Tenant existingTenant = createTestTenant();
        when(tenantRepository.findByTenantId("tenant-1")).thenReturn(Optional.of(existingTenant));
        when(tenantRepository.save(any(Tenant.class))).thenReturn(existingTenant);

        String result = tenantService.updateTenant("tenant-1", "new-policy");

        assertEquals("Tenant Updated Successfully", result);
        verify(tenantRepository).save(any(Tenant.class));
    }

    @Test
    void testUpdateTenant_NonExistentTenant_ReturnsSuccessMessageButNoSave() {
        when(tenantRepository.findByTenantId("non-existent")).thenReturn(Optional.empty());

        String result = tenantService.updateTenant("non-existent", "new-policy");

        assertEquals("Tenant Updated Successfully", result);
        verify(tenantRepository, never()).save(any(Tenant.class));
    }

    private Tenant createTestTenant() {
        Tenant tenant = new Tenant();
        tenant.setTenantId("tenant-1");
        tenant.setTenantName("Test Tenant");
        tenant.setPolicyIds(Arrays.asList("policy-1", "policy-2"));
        return tenant;
    }
}
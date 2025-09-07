package com.smarsh.compliance.controller;


import com.smarsh.compliance.entity.Tenant;
import com.smarsh.compliance.service.TenantService;
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
class TenantControllerTest {

    @Mock
    private TenantService tenantService;

    private TenantController tenantController;

    @BeforeEach
    void setUp() {
        tenantController = new TenantController(tenantService);
    }

    // 1. Test adding null tenant returns an error message
    @Test
    void testAddTenant_NullTenant_ReturnsError() {
        when(tenantService.addTenant(null)).thenReturn("Tenant cannot be null");

        String result = tenantController.addTenant(null);

        assertEquals("Tenant cannot be null", result);
        verify(tenantService).addTenant(null);
    }

    // 2. Test adding duplicate tenant returns duplicate message
    @Test
    void testAddTenant_DuplicateTenant_ReturnsDuplicateMessage() {
        Tenant tenant = new Tenant();
        tenant.setTenantName("Duplicate Tenant");

        when(tenantService.addTenant(any(Tenant.class))).thenReturn("Tenant already exists");

        String result = tenantController.addTenant(tenant);

        assertEquals("Tenant already exists", result);
        verify(tenantService).addTenant(tenant);
    }

    // 3. Test adding tenant with empty name returns validation message
    @Test
    void testAddTenant_EmptyTenantName_ReturnsValidationMessage() {
        Tenant tenant = new Tenant();
        tenant.setTenantName("");

        when(tenantService.addTenant(any(Tenant.class))).thenReturn("Tenant name cannot be empty");

        String result = tenantController.addTenant(tenant);

        assertEquals("Tenant name cannot be empty", result);
        verify(tenantService).addTenant(tenant);
    }

    // 4. Test getAllTenant when service throws exception
    @Test
    void testGetAllTenant_ServiceThrowsException() {
        when(tenantService.getAllTenant()).thenThrow(new RuntimeException("Database error"));

        Exception exception = assertThrows(RuntimeException.class, () -> tenantController.getAllTenant());
        assertEquals("Database error", exception.getMessage());
        verify(tenantService).getAllTenant();
    }

    // 5. Test addTenant returns correct response type (String)
    @Test
    void testAddTenant_ResponseTypeIsString() {
        Tenant tenant = new Tenant();
        tenant.setTenantName("Test Tenant");

        when(tenantService.addTenant(any(Tenant.class))).thenReturn("Tenant added successfully");

        String result = tenantController.addTenant(tenant);

        assertNotNull(result);
        assertTrue(result instanceof String);
        assertEquals("Tenant added successfully", result);
    }

    @Test
    void testAddTenant_ValidTenant_ReturnsSuccess() {
        Tenant tenant = new Tenant();
        tenant.setTenantName("Test Tenant");

        when(tenantService.addTenant(any(Tenant.class))).thenReturn("Tenant added successfully");

        String result = tenantController.addTenant(tenant);

        assertEquals("Tenant added successfully", result);
        verify(tenantService).addTenant(tenant);
    }

    @Test
    void testGetAllTenant_ReturnsTenants() {
        Tenant tenant1 = new Tenant();
        tenant1.setTenantId("tenant-1");
        Tenant tenant2 = new Tenant();
        tenant2.setTenantId("tenant-2");

        when(tenantService.getAllTenant()).thenReturn(Arrays.asList(tenant1, tenant2));

        List<Tenant> result = tenantController.getAllTenant();

        assertEquals(2, result.size());
        verify(tenantService).getAllTenant();
    }

    @Test
    void testGetAllTenant_Empty_ReturnsEmptyList() {
        when(tenantService.getAllTenant()).thenReturn(List.of());

        List<Tenant> result = tenantController.getAllTenant();

        assertTrue(result.isEmpty());
    }
}
package com.smarsh.compliance.controller;

import com.smarsh.compliance.entity.Tenant;
import com.smarsh.compliance.service.TenantService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class TenantController {

    private TenantService tenantService;

    public TenantController(TenantService tenantService) {
        this.tenantService = tenantService;
    }

    @PostMapping("/api/public/tenant")
    public org.springframework.http.ResponseEntity<String> addTenant(@RequestBody Tenant tenant) {
        String result = tenantService.addTenant(tenant);
        return org.springframework.http.ResponseEntity.ok(result);
    }

    @GetMapping("/api/public/tenant")
    public org.springframework.http.ResponseEntity<List<Tenant>> getAllTenant() {
        List<Tenant> tenants = tenantService.getAllTenant();
        return org.springframework.http.ResponseEntity.ok(tenants);
    }
}

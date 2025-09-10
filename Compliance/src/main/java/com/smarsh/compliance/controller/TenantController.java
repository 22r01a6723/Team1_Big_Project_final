package com.smarsh.compliance.controller;

import com.smarsh.compliance.entity.Tenant;
import com.smarsh.compliance.service.TenantService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/public/tenant")
public class TenantController {

    private final TenantService tenantService;

    public TenantController(TenantService tenantService) {
        this.tenantService = tenantService;
    }

    @PostMapping
    public ResponseEntity<String> addTenant(@RequestBody Tenant tenant) {
        String result = tenantService.addTenant(tenant);
        return ResponseEntity.ok(result);
    }

    @GetMapping
    public ResponseEntity<List<Tenant>> getAllTenant() {
        List<Tenant> tenants = tenantService.getAllTenant();
        return ResponseEntity.ok(tenants);
    }
}

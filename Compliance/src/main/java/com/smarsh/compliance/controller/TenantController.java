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
    public String addTenant(@RequestBody Tenant tenant) {
        return tenantService.addTenant(tenant);
    }

    @GetMapping("/api/public/tenant")
    public List<Tenant> getAllTenant() {
        return tenantService.getAllTenant();
    }
}

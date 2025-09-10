package com.smarsh.compliance.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.smarsh.compliance.entity.Tenant;
import com.smarsh.compliance.service.TenantService;

import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
public class TenantController {

    private TenantService tenantService;

    public TenantController(TenantService tenantService) {
        this.tenantService = tenantService;
    }

    @PostMapping("/api/public/tenant")
    public org.springframework.http.ResponseEntity<String> addTenant(@RequestBody Tenant tenant) {
        log.info("START addTenant tenantId={}", tenant.getTenantId());
        String result = tenantService.addTenant(tenant);
        log.info("END addTenant tenantId={} result={} ", tenant.getTenantId(), result);
        return org.springframework.http.ResponseEntity.ok(result);
    }

    @GetMapping("/api/public/tenant")
    public org.springframework.http.ResponseEntity<List<Tenant>> getAllTenant() {
        log.info("FETCH getAllTenant");
        List<Tenant> tenants = tenantService.getAllTenant();
        return org.springframework.http.ResponseEntity.ok(tenants);
    }
}

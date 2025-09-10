package com.smarsh.compliance.service;

import com.smarsh.compliance.entity.Tenant;
import com.smarsh.compliance.repository.TenantRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TenantService {

    private final TenantRepository tenantRepository;

    public TenantService(TenantRepository tenantRepository) {
        this.tenantRepository = tenantRepository;
    }

    public String addTenant(Tenant tenant) {
        try {
            tenantRepository.save(tenant);
            return "Tenant added successfully";
        } catch (Exception e) {
            throw new com.smarsh.compliance.exception.ComplianceException("Error adding tenant: " + e.getMessage(), e);
        }
    }

    public List<Tenant> getAllTenant() {
        try {
            return tenantRepository.findAll();
        } catch (Exception e) {
            throw new com.smarsh.compliance.exception.ComplianceException("Error fetching tenants: " + e.getMessage(), e);
        }
    }
}

package com.smarsh.compliance.service;

import co.elastic.clients.elasticsearch.license.LicenseStatus;
import com.smarsh.compliance.entity.Tenant;
import com.smarsh.compliance.repository.TenantRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TenantService {

    private TenantRepository tenantRepository;
    public TenantService(TenantRepository tenantRepository) {
        this.tenantRepository = tenantRepository;
    }

    public String addTenant(Tenant tenant) {
        try {
            tenantRepository.save(tenant);
            return "Tenant added successfully";
        } catch (Exception e) {
            org.slf4j.LoggerFactory.getLogger(TenantService.class).error("Error adding tenant", e);
            throw new com.smarsh.compliance.exception.ComplianceException("Error adding tenant: " + e.getMessage(), e);
        }
    }

    public List<Tenant> getAllTenant() {
        try {
            return tenantRepository.findAll();
        } catch (Exception e) {
            org.slf4j.LoggerFactory.getLogger(TenantService.class).error("Error fetching tenants", e);
            throw new com.smarsh.compliance.exception.ComplianceException("Error fetching tenants: " + e.getMessage(), e);
        }
    }

    public String updateTenant(String id, String ruleId) {
        try {
            boolean updated = tenantRepository.findByTenantId(id).map(tenant -> {
                // You may want to update tenant fields here using ruleId
                tenantRepository.save(tenant);
                return true;
            }).orElse(false);
            if (!updated) {
                throw new com.smarsh.compliance.exception.ComplianceNotFoundException("Tenant not found for id: " + id);
            }
            return "Tenant Updated Successfully";
        } catch (com.smarsh.compliance.exception.ComplianceNotFoundException nf) {
            throw nf;
        } catch (Exception e) {
            org.slf4j.LoggerFactory.getLogger(TenantService.class).error("Error updating tenant", e);
            throw new com.smarsh.compliance.exception.ComplianceException("Error updating tenant: " + e.getMessage(), e);
        }
    }
}

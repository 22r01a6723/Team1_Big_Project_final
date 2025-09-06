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
        tenantRepository.save(tenant);
        return "Tenant added successfully";
    }

    public List<Tenant> getAllTenant() {
        return tenantRepository.findAll();
    }

    public String updateTenant(String id,String ruleId) {
        tenantRepository.findByTenantId(id).ifPresent(tenant -> {tenantRepository.save(tenant);});
        return "Tenant Updated Successfully";
    }
}

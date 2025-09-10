package com.smarsh.compliance.repository;

import com.smarsh.compliance.entity.TenantPolicy;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TenantPolicyRepository extends JpaRepository<TenantPolicy, String> {
    List<TenantPolicy> findByTenantId(String tenantId);
}

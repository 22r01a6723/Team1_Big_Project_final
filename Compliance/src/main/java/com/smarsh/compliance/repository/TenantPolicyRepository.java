package com.smarsh.compliance.repository;

import com.smarsh.compliance.entity.TenantPolicy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TenantPolicyRepository extends JpaRepository<TenantPolicy,String> {

    List<String> findByTenantId(String tenantId);
}

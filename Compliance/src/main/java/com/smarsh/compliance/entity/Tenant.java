package com.smarsh.compliance.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
public class Tenant {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String tenantId;
    private String tenantName;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "tenant_policy_ids",
            joinColumns = @JoinColumn(name = "tenant_policy_id")
    )
    @Column(name = "policy_id")
    private List<String> policyIds = new ArrayList<>();
}

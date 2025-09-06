package com.smarsh.compliance.entity;

import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;

@Embeddable
@Getter
@Setter
public class PolicyCondition {
    private String networkEquals;
}

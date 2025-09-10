package com.smarsh.compliance.entity;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@DiscriminatorValue("regex")
public class RegexPolicy extends Policy {
    private String pattern;
}

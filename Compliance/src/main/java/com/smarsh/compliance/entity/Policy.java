package com.smarsh.compliance.entity;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "type", discriminatorType = DiscriminatorType.STRING)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type", visible = true)
@JsonSubTypes({
        @JsonSubTypes.Type(value = KeywordPolicy.class, name = "keyword"),
        @JsonSubTypes.Type(value = RegexPolicy.class, name = "regex")
})
public class Policy {

    @Id
    private String ruleId;
    @Column(name = "type", insertable = false, updatable = false)
    private String type;
    private String description;
    private String field;
    private String version;

    @Embedded
    private PolicyCondition when;
}

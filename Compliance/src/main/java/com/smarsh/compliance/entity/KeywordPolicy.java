package com.smarsh.compliance.entity;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.DiscriminatorValue;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@DiscriminatorValue("keyword")
public class KeywordPolicy extends Policy {

    @ElementCollection(fetch = jakarta.persistence.FetchType.EAGER)
    @CollectionTable(name = "keywords", joinColumns = @JoinColumn(name = "ruleId"))
    @Column(name = "keyword")
    private List<String> keywords = new ArrayList<>();
}

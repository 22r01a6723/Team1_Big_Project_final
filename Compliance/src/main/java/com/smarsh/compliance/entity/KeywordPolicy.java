package com.smarsh.compliance.entity;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@Entity
@DiscriminatorValue("keyword")
public class KeywordPolicy extends Policy {
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "keywords",
            joinColumns = @JoinColumn(name = "ruleId")
    )
    @Column(name = "keyword")
    private List<String> keywords = new ArrayList<>();


}

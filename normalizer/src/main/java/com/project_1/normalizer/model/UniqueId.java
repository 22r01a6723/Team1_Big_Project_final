package com.project_1.normalizer.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "uniqueIDs")
public class UniqueId {

    @Id
    private String id; // messageId

    public UniqueId(String id) {
        this.id = id;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
}

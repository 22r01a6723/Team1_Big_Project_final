// models/ProcessedMessage.java
package com.Project1.IngestionAndValidation.Models;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "processed_messages")
public class ProcessedMessage {
    @Id
    private String id;   // stableMessageId

    private String tenantId;
    private String network;

    public ProcessedMessage(String id, String tenantId, String network) {
        this.id = id;
        this.tenantId = tenantId;
        this.network = network;
    }

    public String getId() {
        return id;
    }

    public String getTenantId() {
        return tenantId;
    }

    public String getNetwork() {
        return network;
    }
}


package com.smarsh.compliance.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Flag {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String flagId;
    private String flagDescription;
    private String ruleId;
    private String messageId;
    private String tenantId;
    private String network;

    private Long createdAt;

    public Flag() {}

    public Flag(String ruleId, String messageId, String flagDescription, String network, String tenantId) {
        this.flagDescription = flagDescription;
        this.ruleId = ruleId;
        this.messageId = messageId;
        this.createdAt = java.time.Instant.now().toEpochMilli();
        this.tenantId = tenantId;
        this.network = network;
    }
}

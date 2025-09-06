package com.Project_1.Review.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "flag")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Flag {

    @Id
    @Column(name = "flag_id")
    private String flagId;

    @Column(name = "flag_description")
    private String flagDescription;

    @Column(name = "rule_id")
    private String ruleId;

    @Column(name = "message_id")
    private String messageId;

    @Column(name = "tenant_id")
    private String tenantId;

    @Column(name = "network")
    private String network;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Long createdAt;
}

package com.complyvault.retention.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@Document(collection = "retention_policies")
public class RetentionPolicy {
    @Id
    private String id;
    private String tenantId;
    private String channel;
    private int retentionPeriodDays;
}

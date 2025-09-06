package com.project_1.normalizer.Retention.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

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

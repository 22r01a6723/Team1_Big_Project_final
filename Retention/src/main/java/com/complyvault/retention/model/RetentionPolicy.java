package com.complyvault.retention.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
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
    @NotNull(message = "tenantId is required")
    private String tenantId;
    @NotNull(message = "channel is required")
    private String channel;
    @Min(value = 1, message = "retentionPeriodDays must be at least 1")
    private int retentionPeriodDays;
}

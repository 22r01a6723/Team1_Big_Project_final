package com.complyvault.shared.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RetentionPolicyDTO {
    
    private String id;
    private String tenantId;
    private String channel;
    private int retentionPeriodDays;
}

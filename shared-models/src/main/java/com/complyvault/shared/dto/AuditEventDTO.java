package com.complyvault.shared.dto;

import lombok.*;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.Instant;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuditEventDTO {
    
    private String tenantId;
    private String messageId;
    private String network;
    private String eventType;
    private String performedBy;
    private String serviceName;
    
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "UTC")
    private Instant timestamp;
    
    private Map<String, Object> details;
}

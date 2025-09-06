/*package com.Project1.IngestionAndValidation.Models;

import com.Project1.IngestionAndValidation.Validation.Validator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public abstract class BaseMessageDTO implements Validator {
    private String tenantId;

    public String getTenantId() { return tenantId; }
    public void setTenantId(String tenantId) { this.tenantId = tenantId; }

    @Override
    public String toString() {
        return "BaseMessageDTO{" +
                "tenantId='" + tenantId + '\'' +
                '}';
    }
}*/


package com.Project1.IngestionAndValidation.Models;

import com.Project1.IngestionAndValidation.Validation.MessageValidator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public abstract class BaseMessageDTO {
    private String tenantId;
    private String stableMessageId;   // <--- used for deduplication

    public String getTenantId() { return tenantId; }

    @Override
    public String toString() {
        return "BaseMessageDTO{" +
                "tenantId='" + tenantId + '\'' +
                ", stableMessageId='" + stableMessageId + '\'' +
                '}';
    }
}

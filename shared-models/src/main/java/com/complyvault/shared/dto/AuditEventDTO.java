//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.complyvault.shared.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;
import java.time.Instant;
import java.util.Map;
import lombok.Generated;

public class AuditEventDTO {
    private String tenantId;
    private String messageId;
    private String network;
    private String eventType;
    private String performedBy;
    private String serviceName;
    @JsonFormat(
            shape = Shape.STRING,
            pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
            timezone = "UTC"
    )
    private Instant timestamp;
    private Map<String, Object> details;

    @Generated
    public static AuditEventDTOBuilder builder() {
        return new AuditEventDTOBuilder();
    }

    @Generated
    public String getTenantId() {
        return this.tenantId;
    }

    @Generated
    public String getMessageId() {
        return this.messageId;
    }

    @Generated
    public String getNetwork() {
        return this.network;
    }

    @Generated
    public String getEventType() {
        return this.eventType;
    }

    @Generated
    public String getPerformedBy() {
        return this.performedBy;
    }

    @Generated
    public String getServiceName() {
        return this.serviceName;
    }

    @Generated
    public Instant getTimestamp() {
        return this.timestamp;
    }

    @Generated
    public Map<String, Object> getDetails() {
        return this.details;
    }

    @Generated
    public void setTenantId(final String tenantId) {
        this.tenantId = tenantId;
    }

    @Generated
    public void setMessageId(final String messageId) {
        this.messageId = messageId;
    }

    @Generated
    public void setNetwork(final String network) {
        this.network = network;
    }

    @Generated
    public void setEventType(final String eventType) {
        this.eventType = eventType;
    }

    @Generated
    public void setPerformedBy(final String performedBy) {
        this.performedBy = performedBy;
    }

    @Generated
    public void setServiceName(final String serviceName) {
        this.serviceName = serviceName;
    }

    @JsonFormat(
            shape = Shape.STRING,
            pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
            timezone = "UTC"
    )
    @Generated
    public void setTimestamp(final Instant timestamp) {
        this.timestamp = timestamp;
    }

    @Generated
    public void setDetails(final Map<String, Object> details) {
        this.details = details;
    }

    @Generated
    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        } else if (!(o instanceof AuditEventDTO)) {
            return false;
        } else {
            AuditEventDTO other = (AuditEventDTO)o;
            if (!other.canEqual(this)) {
                return false;
            } else {
                Object this$tenantId = this.getTenantId();
                Object other$tenantId = other.getTenantId();
                if (this$tenantId == null) {
                    if (other$tenantId != null) {
                        return false;
                    }
                } else if (!this$tenantId.equals(other$tenantId)) {
                    return false;
                }

                Object this$messageId = this.getMessageId();
                Object other$messageId = other.getMessageId();
                if (this$messageId == null) {
                    if (other$messageId != null) {
                        return false;
                    }
                } else if (!this$messageId.equals(other$messageId)) {
                    return false;
                }

                Object this$network = this.getNetwork();
                Object other$network = other.getNetwork();
                if (this$network == null) {
                    if (other$network != null) {
                        return false;
                    }
                } else if (!this$network.equals(other$network)) {
                    return false;
                }

                Object this$eventType = this.getEventType();
                Object other$eventType = other.getEventType();
                if (this$eventType == null) {
                    if (other$eventType != null) {
                        return false;
                    }
                } else if (!this$eventType.equals(other$eventType)) {
                    return false;
                }

                Object this$performedBy = this.getPerformedBy();
                Object other$performedBy = other.getPerformedBy();
                if (this$performedBy == null) {
                    if (other$performedBy != null) {
                        return false;
                    }
                } else if (!this$performedBy.equals(other$performedBy)) {
                    return false;
                }

                Object this$serviceName = this.getServiceName();
                Object other$serviceName = other.getServiceName();
                if (this$serviceName == null) {
                    if (other$serviceName != null) {
                        return false;
                    }
                } else if (!this$serviceName.equals(other$serviceName)) {
                    return false;
                }

                Object this$timestamp = this.getTimestamp();
                Object other$timestamp = other.getTimestamp();
                if (this$timestamp == null) {
                    if (other$timestamp != null) {
                        return false;
                    }
                } else if (!this$timestamp.equals(other$timestamp)) {
                    return false;
                }

                Object this$details = this.getDetails();
                Object other$details = other.getDetails();
                if (this$details == null) {
                    if (other$details != null) {
                        return false;
                    }
                } else if (!this$details.equals(other$details)) {
                    return false;
                }

                return true;
            }
        }
    }

    @Generated
    protected boolean canEqual(final Object other) {
        return other instanceof AuditEventDTO;
    }

    @Generated
    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        Object $tenantId = this.getTenantId();
        result = result * 59 + ($tenantId == null ? 43 : $tenantId.hashCode());
        Object $messageId = this.getMessageId();
        result = result * 59 + ($messageId == null ? 43 : $messageId.hashCode());
        Object $network = this.getNetwork();
        result = result * 59 + ($network == null ? 43 : $network.hashCode());
        Object $eventType = this.getEventType();
        result = result * 59 + ($eventType == null ? 43 : $eventType.hashCode());
        Object $performedBy = this.getPerformedBy();
        result = result * 59 + ($performedBy == null ? 43 : $performedBy.hashCode());
        Object $serviceName = this.getServiceName();
        result = result * 59 + ($serviceName == null ? 43 : $serviceName.hashCode());
        Object $timestamp = this.getTimestamp();
        result = result * 59 + ($timestamp == null ? 43 : $timestamp.hashCode());
        Object $details = this.getDetails();
        result = result * 59 + ($details == null ? 43 : $details.hashCode());
        return result;
    }

    @Generated
    public String toString() {
        String var10000 = this.getTenantId();
        return "AuditEventDTO(tenantId=" + var10000 + ", messageId=" + this.getMessageId() + ", network=" + this.getNetwork() + ", eventType=" + this.getEventType() + ", performedBy=" + this.getPerformedBy() + ", serviceName=" + this.getServiceName() + ", timestamp=" + String.valueOf(this.getTimestamp()) + ", details=" + String.valueOf(this.getDetails()) + ")";
    }

    @Generated
    public AuditEventDTO() {
    }

    @Generated
    public AuditEventDTO(final String tenantId, final String messageId, final String network, final String eventType, final String performedBy, final String serviceName, final Instant timestamp, final Map<String, Object> details) {
        this.tenantId = tenantId;
        this.messageId = messageId;
        this.network = network;
        this.eventType = eventType;
        this.performedBy = performedBy;
        this.serviceName = serviceName;
        this.timestamp = timestamp;
        this.details = details;
    }

    @Generated
    public static class AuditEventDTOBuilder {
        @Generated
        private String tenantId;
        @Generated
        private String messageId;
        @Generated
        private String network;
        @Generated
        private String eventType;
        @Generated
        private String performedBy;
        @Generated
        private String serviceName;
        @Generated
        private Instant timestamp;
        @Generated
        private Map<String, Object> details;

        @Generated
        AuditEventDTOBuilder() {
        }

        @Generated
        public AuditEventDTOBuilder tenantId(final String tenantId) {
            this.tenantId = tenantId;
            return this;
        }

        @Generated
        public AuditEventDTOBuilder messageId(final String messageId) {
            this.messageId = messageId;
            return this;
        }

        @Generated
        public AuditEventDTOBuilder network(final String network) {
            this.network = network;
            return this;
        }

        @Generated
        public AuditEventDTOBuilder eventType(final String eventType) {
            this.eventType = eventType;
            return this;
        }

        @Generated
        public AuditEventDTOBuilder performedBy(final String performedBy) {
            this.performedBy = performedBy;
            return this;
        }

        @Generated
        public AuditEventDTOBuilder serviceName(final String serviceName) {
            this.serviceName = serviceName;
            return this;
        }

        @JsonFormat(
                shape = Shape.STRING,
                pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
                timezone = "UTC"
        )
        @Generated
        public AuditEventDTOBuilder timestamp(final Instant timestamp) {
            this.timestamp = timestamp;
            return this;
        }

        @Generated
        public AuditEventDTOBuilder details(final Map<String, Object> details) {
            this.details = details;
            return this;
        }

        @Generated
        public AuditEventDTO build() {
            return new AuditEventDTO(this.tenantId, this.messageId, this.network, this.eventType, this.performedBy, this.serviceName, this.timestamp, this.details);
        }

        @Generated
        public String toString() {
            String var10000 = this.tenantId;
            return "AuditEventDTO.AuditEventDTOBuilder(tenantId=" + var10000 + ", messageId=" + this.messageId + ", network=" + this.network + ", eventType=" + this.eventType + ", performedBy=" + this.performedBy + ", serviceName=" + this.serviceName + ", timestamp=" + String.valueOf(this.timestamp) + ", details=" + String.valueOf(this.details) + ")";
        }
    }
}
 
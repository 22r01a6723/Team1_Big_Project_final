
package com.Project1.IngestionAndValidation.Models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class EmailDTO extends BaseMessageDTO {

    private String network;
    private Payload payload;

    public String getNetwork() { return network; }
    public void setNetwork(String network) { this.network = network; }

    public Payload getPayload() { return payload; }
    public void setPayload(Payload payload) { this.payload = payload; }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Payload {
        private String from;
        private List<String> to;
        private String subject;
        private String body;
        private String sentAt;

        public String getFrom() { return from; }
        public void setFrom(String from) { this.from = from; }

        public List<String> getTo() { return to; }
        public void setTo(List<String> to) { this.to = to; }

        public String getSubject() { return subject; }
        public void setSubject(String subject) { this.subject = subject; }

        public String getBody() { return body; }
        public void setBody(String body) { this.body = body; }

        public String getSentAt() { return sentAt; }
        public void setSentAt(String sentAt) { this.sentAt = sentAt; }
    }

    /*@Override
    public boolean validate() {
        return isNotBlank(getTenantId()) &&
                isNotBlank(network) &&
                payload != null &&
                isNotBlank(payload.getFrom()) &&
                payload.getTo() != null && !payload.getTo().isEmpty() &&
                isNotBlank(payload.getSubject()) &&
                isNotBlank(payload.getBody()) &&
                isNotBlank(payload.getSentAt());
    }*/

//    @Override
//    public boolean validate() {
//        boolean valid = isNotBlank(getTenantId()) &&
//                isNotBlank(network) &&
//                payload != null &&
//                isNotBlank(payload.getFrom()) &&
//                payload.getTo() != null && !payload.getTo().isEmpty() &&
//                isNotBlank(payload.getSubject()) &&
//                isNotBlank(payload.getBody()) &&
//                isNotBlank(payload.getSentAt());
//
//        if (valid) {
//            // generate stable ID
//            Map<String, Object> map = Map.of(
//                    "from", payload.getFrom(),
//                    "to", payload.getTo(),
//                    "subject", payload.getSubject(),
//                    "sentAt", payload.getSentAt()
//            );
//            String id = com.Project1.IngestionAndValidation.utils.MessageIdGenerator
//                    .generate(getTenantId(), network, map);
//            setStableMessageId(id);
//        }
//        return valid;
//    }


    private boolean isNotBlank(String field) {
        return field != null && !field.trim().isEmpty();
    }

    @Override
    public String toString() {
        return "EmailDTO{" +
                "tenantId='" + getTenantId() + '\'' +
                ", network='" + network + '\'' +
                ", payload={from='" + (payload != null ? payload.getFrom() : null) + '\'' +
                ", to=" + (payload != null ? payload.getTo() : null) +
                ", subject='" + (payload != null ? payload.getSubject() : null) + '\'' +
                ", body='" + (payload != null ? payload.getBody() : null) + '\'' +
                ", sentAt='" + (payload != null ? payload.getSentAt() : null) + '\'' +
                "}}";
    }
}






//package com.Project1.IngestionAndValidation.Models;
//
//import com.Project1.IngestionAndValidation.Validation.Validator;
//import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
//import java.util.List;
//
//@JsonIgnoreProperties(ignoreUnknown = true)
//public class EmailDTO implements Validator {
//
//    private String tenantId;
//    private String network;
//    private Payload payload;
//
//    // Getters & Setters
//    public String getTenantId() { return tenantId; }
//    public void setTenantId(String tenantId) { this.tenantId = tenantId; }
//
//    public String getNetwork() { return network; }
//    public void setNetwork(String network) { this.network = network; }
//
//    public Payload getPayload() { return payload; }
//    public void setPayload(Payload payload) { this.payload = payload; }
//
//    // Inner Payload Class
//    @JsonIgnoreProperties(ignoreUnknown = true)
//    public static class Payload {
//        private String from;
//        private List<String> to;
//        private String subject;
//        private String body;
//        private String sentAt;
//
//        // Getters & Setters
//        public String getFrom() { return from; }
//        public void setFrom(String from) { this.from = from; }
//
//        public List<String> getTo() { return to; }
//        public void setTo(List<String> to) { this.to = to; }
//
//        public String getSubject() { return subject; }
//        public void setSubject(String subject) { this.subject = subject; }
//
//        public String getBody() { return body; }
//        public void setBody(String body) { this.body = body; }
//
//        public String getSentAt() { return sentAt; }
//        public void setSentAt(String sentAt) { this.sentAt = sentAt; }
//    }
//
//    @Override
//    public boolean validate() {
//        return isNotBlank(tenantId) &&
//                isNotBlank(network) &&
//                payload != null &&
//                isNotBlank(payload.getFrom()) &&
//                payload.getTo() != null && !payload.getTo().isEmpty() &&
//                isNotBlank(payload.getSubject()) &&
//                isNotBlank(payload.getBody()) &&
//                isNotBlank(payload.getSentAt());
//    }
//
//    private boolean isNotBlank(String field) {
//        return field != null && !field.trim().isEmpty();
//    }
//
//    @Override
//    public String toString() {
//        return "EmailDTO{" +
//                "tenantId='" + tenantId + '\'' +
//                ", network='" + network + '\'' +
//                ", payload={from='" + (payload != null ? payload.getFrom() : null) + '\'' +
//                ", to=" + (payload != null ? payload.getTo() : null) +
//                ", subject='" + (payload != null ? payload.getSubject() : null) + '\'' +
//                ", body='" + (payload != null ? payload.getBody() : null) + '\'' +
//                ", sentAt='" + (payload != null ? payload.getSentAt() : null) + '\'' +
//                "}}";
//    }
//}

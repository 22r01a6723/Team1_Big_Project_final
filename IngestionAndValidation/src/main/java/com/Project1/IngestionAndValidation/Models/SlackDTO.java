
package com.Project1.IngestionAndValidation.Models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;

import java.util.Map;

@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class SlackDTO extends BaseMessageDTO {

    private String messageId;
    private String network;
    private String user;
    private String text;
    private String timestamp;
    private String team;
    private String channel;
    private String rawReference;

    public void setMessageId(String messageId) { this.messageId = messageId; }

    public void setNetwork(String network) { this.network = network; }

    public void setUser(String user) { this.user = user; }

    public void setText(String text) { this.text = text; }

    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }

    public void setTeam(String team) { this.team = team; }

    public void setChannel(String channel) { this.channel = channel; }

    public void setRawReference(String rawReference) { this.rawReference = rawReference; }

    /*@Override
    public boolean validate() {
        return isNotBlank(getTenantId()) &&
                isNotBlank(network) &&
                isNotBlank(messageId) &&
                isNotBlank(user) &&
                isNotBlank(text) &&
                isNotBlank(timestamp) &&
                isNotBlank(team) &&
                isNotBlank(channel) &&
                isNotBlank(rawReference);
    }*/
//    @Override
//    public boolean validate() {
//        boolean valid = isNotBlank(getTenantId()) &&
//                isNotBlank(network) &&
//                isNotBlank(user) &&
//                isNotBlank(text) &&
//                isNotBlank(timestamp) &&
//                isNotBlank(team) &&
//                isNotBlank(channel);
//
//        if (valid) {
//            String id = com.Project1.IngestionAndValidation.utils.MessageIdGenerator
//                    .generate();
//            setStableMessageId(id);
//        }
//        return valid;
//    }


    public boolean isNotBlank(String field) {
        return field != null && !field.trim().isEmpty();
    }

    @Override
    public String toString() {
        return "SlackDTO{" +
                "tenantId='" + getTenantId() + '\'' +
                ", network='" + network + '\'' +
                ", messageId='" + messageId + '\'' +
                ", user='" + user + '\'' +
                ", text='" + text + '\'' +
                ", timestamp='" + timestamp + '\'' +
                ", team='" + team + '\'' +
                ", channel='" + channel + '\'' +
                ", rawReference='" + rawReference + '\'' +
                '}';
    }
}



//package com.Project1.IngestionAndValidation.Models;
//
//import com.Project1.IngestionAndValidation.Validation.Validator;
//import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
//
//@JsonIgnoreProperties(ignoreUnknown = true)
//public class SlackDTO implements Validator {
//
//    private String messageId;
//    private String tenantId;
//    private String network;
//    private String user;
//    private String text;
//    private String timestamp;
//    private String team;
//    private String channel;
//    private String rawReference;
//
//    // Getters and setters
//    public String getMessageId() { return messageId; }
//    public void setMessageId(String messageId) { this.messageId = messageId; }
//
//    public String getTenantId() { return tenantId; }
//    public void setTenantId(String tenantId) { this.tenantId = tenantId; }
//
//    public String getNetwork() { return network; }
//    public void setNetwork(String network) { this.network = network; }
//
//    public String getUser() { return user; }
//    public void setUser(String user) { this.user = user; }
//
//    public String getText() { return text; }
//    public void setText(String text) { this.text = text; }
//
//    public String getTimestamp() { return timestamp; }
//    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }
//
//    public String getTeam() { return team; }
//    public void setTeam(String team) { this.team = team; }
//
//    public String getChannel() { return channel; }
//    public void setChannel(String channel) { this.channel = channel; }
//
//    public String getRawReference() { return rawReference; }
//    public void setRawReference(String rawReference) { this.rawReference = rawReference; }
//
//    @Override
//    public boolean validate() {
//        return isNotBlank(messageId) &&
//                isNotBlank(tenantId) &&
//                isNotBlank(network) &&
//                isNotBlank(user) &&
//                isNotBlank(text) &&
//                isNotBlank(timestamp) &&
//                isNotBlank(team) &&
//                isNotBlank(channel) &&
//                isNotBlank(rawReference);
//    }
//
//    private boolean isNotBlank(String field) {
//        return field != null && !field.trim().isEmpty();
//    }
//
//    @Override
//    public String toString() {
//        return "SlackDTO{" +
//                "messageId='" + messageId + '\'' +
//                ", tenantId='" + tenantId + '\'' +
//                ", network='" + network + '\'' +
//                ", user='" + user + '\'' +
//                ", text='" + text + '\'' +
//                ", timestamp='" + timestamp + '\'' +
//                ", team='" + team + '\'' +
//                ", channel='" + channel + '\'' +
//                ", rawReference='" + rawReference + '\'' +
//                '}';
//    }
//}

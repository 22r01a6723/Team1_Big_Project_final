package com.complyvault.retention.model;

import java.time.Instant;
import java.util.List;

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
@Document(collection = "messages")
public class CanonicalMessage {
    @Id
    private String messageId;
    private String tenantId;
    private String network;
    private Instant timestamp;
    private List<Participant> participants;
    private Content content;
    private Context context;
    private Boolean expired;

    @Getter
    @Setter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    public static class Participant {
        private String role;
        private String id;
        private String displayName;
    }

    @Getter
    @Setter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    public static class Content {
        private String subject;
        private String body;
    }

    @Getter
    @Setter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    public static class Context {
        private String team;
        private String channel;
        private String rawReference;
    }
}

package com.project_1.normalizer.model;


import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.kafka.annotation.EnableKafka;

import java.time.Instant;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@Document(collection = "canonical_messages")
public class CanonicalMessage {
    @Id
    private String messageId;
    private String tenantId;
    private String network;
    private Instant timestamp;
    private List<Participant> participants;
    private Content content;
    private Context context;
    @CreatedDate
    private Instant createdAt;

    @Getter
    @Setter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    public static class Participant {
        private String role;        // sender, recipient, cc, bcc
        private String id;          // email, username, etc.
        private String displayName; // optional
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


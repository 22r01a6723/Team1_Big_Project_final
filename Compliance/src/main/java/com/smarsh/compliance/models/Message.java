package com.smarsh.compliance.models;


import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.kafka.annotation.EnableKafka;

import java.time.Instant;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Message {
    @Id
    private String messageId;
    private String tenantId;
    private String network;
    private Instant timestamp;
    private List<Participant> participants;
    private Content content;
    private Context context;
    private boolean flagged=false;
    private FlagInfo flagInfo;
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


    @Getter
    @Setter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    public static class FlagInfo {
        private String flagDescription;
        private Instant timestamp;
    }
}


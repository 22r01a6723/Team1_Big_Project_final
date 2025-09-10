package com.smarsh.compliance.models;

import lombok.*;
import java.time.Instant;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Message {
    private String messageId;
    private String tenantId;
    private String network;
    private Instant timestamp;
    private List<Participant> participants;
    private Content content;
    private Context context;
    private boolean flagged = false;
    private FlagInfo flagInfo;
    private Instant createdAt;

    @Getter
    @Setter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
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
    public static class Content {
        private String subject;
        private String body;
    }

    @Getter
    @Setter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
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
    public static class FlagInfo {
        private String flagDescription;
        private Instant timestamp;
    }
}

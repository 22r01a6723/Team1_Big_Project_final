package com.Project1.Search.model;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;


import java.time.Instant;
import java.util.List;

@Document(indexName = "messages")  // Elasticsearch index
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
    private List<Participant> participants;
    private Content content;
    private Context context;
    private boolean flagged = false;
    private FlagInfo flagInfo;
    private String network;
    @Field(type = FieldType.Date, format = DateFormat.epoch_millis)
    private Instant timestamp;

    @Field(type = FieldType.Date, format = DateFormat.epoch_millis)
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
        @Field(type = FieldType.Date, format = DateFormat.epoch_millis)
        private Instant timestamp;    }
}

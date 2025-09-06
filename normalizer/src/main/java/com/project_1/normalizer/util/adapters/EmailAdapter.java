package com.project_1.normalizer.util.adapters;

import com.fasterxml.jackson.databind.JsonNode;
import com.project_1.normalizer.model.CanonicalMessage;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;


@Service
public class EmailAdapter implements MessageAdapter {
    @Override
    public boolean supports(String network) {
        return "email".equalsIgnoreCase(network);
    }

    @Override
    public CanonicalMessage map(JsonNode root) {
        JsonNode payload = root.get("payload");

        List<CanonicalMessage.Participant> participants = new ArrayList<>();
        participants.add(CanonicalMessage.Participant.builder()
                .role("sender")
                .id(payload.get("from").asText())
                .build());

        payload.get("to").forEach(toNode ->
                participants.add(CanonicalMessage.Participant.builder()
                        .role("recipient")
                        .id(toNode.asText())
                        .build())
        );

        return CanonicalMessage.builder()
                .messageId(root.get("stableMessageId").asText())
                .tenantId(root.get("tenantId").asText())
                .network("email")
                .timestamp(Instant.parse(payload.get("sentAt").asText()))
                .participants(participants)
                .content(CanonicalMessage.Content.builder()
                        .subject(payload.get("subject").asText())
                        .body(payload.get("body").asText())
                        .build())
                .context(CanonicalMessage.Context.builder().build())
                .build();
    }
}


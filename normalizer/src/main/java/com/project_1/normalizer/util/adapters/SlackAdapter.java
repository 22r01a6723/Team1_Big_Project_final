package com.project_1.normalizer.util.adapters;

import java.time.Instant;
import java.util.List;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.project_1.normalizer.model.CanonicalMessage;

@Service
public class SlackAdapter implements MessageAdapter {
    @Override
    public boolean supports(String network) {
        return "slack".equalsIgnoreCase(network);
    }

    @Override
    public CanonicalMessage map(JsonNode root) {
        return CanonicalMessage.builder()
                .messageId(root.has("stableMessageId") ? root.get("stableMessageId").asText() : root.get("messageId").asText())
                .tenantId(root.get("tenantId").asText())
                .network("slack")
                .timestamp(Instant.parse(root.get("timestamp").asText()))
                .participants(List.of(
                        CanonicalMessage.Participant.builder()
                                .role("sender")
                                .id(root.get("user").asText())
                                .build()
                ))
                .content(CanonicalMessage.Content.builder()
                        .body(root.get("text").asText())
                        .build())
                .context(CanonicalMessage.Context.builder()
                        .team(root.has("team") ? root.get("team").asText() : null)
                        .channel(root.has("channel") ? root.get("channel").asText() : null)
                        .rawReference(root.has("rawReference") ? root.get("rawReference").asText() : null)
                        .build())
                .build();
    }
}

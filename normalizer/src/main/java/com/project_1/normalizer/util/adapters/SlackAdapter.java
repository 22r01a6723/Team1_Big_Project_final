package com.project_1.normalizer.util.adapters;

import com.fasterxml.jackson.databind.JsonNode;
import com.project_1.normalizer.model.CanonicalMessage;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public class SlackAdapter implements MessageAdapter {
    @Override
    public boolean supports(String network) {
        return "slack".equalsIgnoreCase(network);
    }

    @Override
    public CanonicalMessage map(JsonNode root) {
        return CanonicalMessage.builder()
                .messageId(root.get("stableMessageId").asText())
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
                        .team(root.get("team").asText())
                        .channel(root.get("channel").asText())
                        .rawReference(root.get("rawReference").asText())
                        .build())
                .build();
    }
}

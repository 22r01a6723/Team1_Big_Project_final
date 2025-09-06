package com.Project1.Search.dto;

import com.Project1.Search.model.Message;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class SearchResponse {
    private String messageId;
    private String tenantId;
    private Instant timestamp;
    private List<Message.Participant> participants;
    private Message.Content content;
    private Message.FlagInfo flagInfo;
    private boolean flagged;
    private String network;
}

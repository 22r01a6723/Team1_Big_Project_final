package com.Project1.Search.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.List;

@Data
@Getter
@Setter
public class SearchRequest {
    // General search
    private String keyword;
    private String tenantId;

    // Field-specific searches
    private String messageId;
    private String subject;
    private String body;
    private String network;
    private Boolean flagged;

    // Context searches
    private String team;
    private String channel;
    private String rawReference;

    // Participant searches
    private List<String> participantIds;
    private List<String> participantRoles;

    // Flag info searches
    private String flagDescription;
    private String ruleId;

    // Time range searches
    private Instant startTime;
    private Instant endTime;
    private Instant startFlagTime;
    private Instant endFlagTime;

    // Pagination
    private int page = 0;
    private int size = 20;

    // Search options
    private boolean exactMatch = false;
    private boolean partialMatch = true;
}
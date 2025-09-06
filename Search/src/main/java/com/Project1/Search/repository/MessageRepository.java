package com.Project1.Search.repository;

import com.Project1.Search.model.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;

@Repository
public interface MessageRepository extends ElasticsearchRepository<Message, String> {


    // Add this method to MessageRepository

    // Full-text search
    @Query("{\"bool\": {\"must\": [{\"query_string\": {\"query\": \"?0\", \"fields\": [\"content.subject^2\", \"content.body^1.5\", \"participants.id\", \"participants.displayName\", \"context.team\", \"context.channel\", \"context.rawReference\", \"flagInfo.flagDescription\"]}}, {\"term\": {\"tenantId.keyword\": \"?1\"}}]}}")
    Page<Message> fullTextSearch(String query, String tenantId, Pageable pageable);

    // Partial match search
    @Query("{\"bool\": {\"must\": [{\"wildcard\": {\"?0\": \"*?1*\"}}, {\"term\": {\"tenantId\": \"?2\"}}]}}")
    Page<Message> partialMatchSearch(String field, String value, String tenantId, Pageable pageable);

    // Exact match search
    @Query("{\"bool\": {\"must\": [{\"match_phrase\": {\"?0\": \"?1\"}}, {\"term\": {\"tenantId\": \"?2\"}}]}}")
    Page<Message> exactMatchSearch(String field, String value, String tenantId, Pageable pageable);

    // Field-specific searches
    Page<Message> findByTenantId(String tenantId, Pageable pageable);
    Page<Message> findByTenantIdAndNetwork(String tenantId, String network, Pageable pageable);
    Page<Message> findByTenantIdAndFlagged(String tenantId, boolean flagged, Pageable pageable);
    Page<Message> findByTenantIdAndTimestampBetween(String tenantId, Instant start, Instant end, Pageable pageable);

    // Context searches
//    @Query("{\"bool\": {\"must\": [{\"term\": {\"context.team\": \"?0\"}}, {\"term\": {\"tenantId\": \"?1\"}}]}}")
//    Page<Message> findByTeam(String team, String tenantId, Pageable pageable);

    // Corrected query - parameters now match method signature
    @Query("{\"bool\": {\"must\": [{\"term\": {\"context.team.keyword\": \"?0\"}}, {\"term\": {\"tenantId.keyword\": \"?1\"}}]}}")
    Page<Message> findByTeam(String tenantId, String team, Pageable pageable);

    @Query("{\"bool\": {\"must\": [{\"term\": {\"context.channel.keyword\": \"?0\"}}, {\"term\": {\"tenantId.keyword\": \"?1\"}}]}}")
    Page<Message> findByChannel(String channel, String tenantId, Pageable pageable);

    // Participant searches
//    @Query("{\"bool\": {\"must\": [{\"nested\": {\"path\": \"participants\", \"query\": {\"term\": {\"participants.id\": \"?0\"}}}}, {\"term\": {\"tenantId\": \"?1\"}}]}}")
//    Page<Message> findByParticipantId(String participantId, String tenantId, Pageable pageable);

    @Query("{\"bool\": {\"must\": [" +
            "{\"nested\": {\"path\": \"participants\", \"query\": {\"term\": {\"participants.id.keyword\": \"?0\"}}}}," +
            "{\"term\": {\"tenantId.keyword\": \"?1\"}}" +
            "]}}")
    Page<Message> findByParticipantId(String participantId, String tenantId, Pageable pageable);


//    @Query("{\"bool\": {\"must\": [{\"nested\": {\"path\": \"participants\", \"query\": {\"term\": {\"participants.role\": \"?0\"}}}}, {\"term\": {\"tenantId\": \"?1\"}}]}}")
//    Page<Message> findByParticipantRole(String role, String tenantId, Pageable pageable);

    @Query("{\"bool\": {\"must\": [" +
            "{\"term\": {\"participants.role.keyword\": \"?0\"}}," +
            "{\"term\": {\"tenantId.keyword\": \"?1\"}}" +
            "]}}")
    Page<Message> findByParticipantRole(String role, String tenantId, Pageable pageable);

    // Flag info searches
//    @Query("{\"bool\": {\"must\": [{\"term\": {\"flagInfo.flagDescription\": \"?0\"}}, {\"term\": {\"tenantId\": \"?1\"}}]}}")
//    Page<Message> findByFlagDescription(String flagDescription, String tenantId, Pageable pageable);

    @Query("{\"bool\": {\"must\": [" +
            "{\"match\": {\"flagInfo.flagDescription\": \"?0\"}}," +
            "{\"term\": {\"tenantId.keyword\": \"?1\"}}" +
            "]}}")
    Page<Message> findByFlagDescription(String flagDescription, String tenantId, Pageable pageable);

    @Query("{\"bool\": {\"must\": [{\"term\": {\"flagInfo.ruleId\": \"?0\"}}, {\"term\": {\"tenantId\": \"?1\"}}]}}")
    Page<Message> findByRuleId(String ruleId, String tenantId, Pageable pageable);

    Page<Message> findByTenantIdAndFlagInfoTimestampBetween(String tenantId, Instant start, Instant end, Pageable pageable);
}
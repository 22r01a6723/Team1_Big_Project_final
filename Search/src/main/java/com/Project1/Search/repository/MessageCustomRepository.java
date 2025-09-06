package com.Project1.Search.repository;

import com.Project1.Search.dto.SearchRequest;
import com.Project1.Search.model.Message;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.Criteria;
import org.springframework.data.elasticsearch.core.query.CriteriaQuery;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class MessageCustomRepository {

    private final ElasticsearchOperations elasticsearchOperations;

    public Page<Message> advancedSearch(SearchRequest request, Pageable pageable) {
        try {
            System.out.println("=== STARTING SEARCH ===");
            System.out.println("Request: " + request.toString());

            // Always start with tenantId
            Criteria criteria = new Criteria("tenantId.keyword").is(request.getTenantId());

            // KEYWORD SEARCH - Use matches() instead of contains()
            if (StringUtils.hasText(request.getKeyword())) {
                System.out.println("Adding keyword: " + request.getKeyword());
                Criteria keywordCriteria = new Criteria();
                keywordCriteria.or(new Criteria("content.subject").matches(request.getKeyword()))
                        .or(new Criteria("content.body").matches(request.getKeyword()))
                        .or(new Criteria("participants.id").matches(request.getKeyword()))
                        .or(new Criteria("flagInfo.flagDescription").matches(request.getKeyword()))
                        .or(new Criteria("network").matches(request.getKeyword()));
                criteria.and(keywordCriteria);
            }

            // SUBJECT SEARCH - Use proper field names
            if (StringUtils.hasText(request.getSubject())) {
                System.out.println("Adding subject: " + request.getSubject());
                if (request.isExactMatch()) {
                    criteria.and(new Criteria("content.subject.keyword").is(request.getSubject()));
                } else {
                    criteria.and(new Criteria("content.subject").matches(request.getSubject()));
                }
            }

            // BODY SEARCH
            if (StringUtils.hasText(request.getBody())) {
                criteria.and(new Criteria("content.body").matches(request.getBody()));
            }

            // NETWORK FILTER
            if (StringUtils.hasText(request.getNetwork())) {
                criteria.and(new Criteria("network.keyword").is(request.getNetwork()));
            }

            // FLAGGED FILTER
            if (request.getFlagged() != null) {
                criteria.and(new Criteria("flagged").is(request.getFlagged()));
            }

            // PARTICIPANT IDs - Use proper field names and syntax
            if (request.getParticipantIds() != null && !request.getParticipantIds().isEmpty()) {
                System.out.println("Adding participant IDs: " + request.getParticipantIds());
                Criteria participantCriteria = new Criteria("participants.id.keyword");
                participantCriteria.in(request.getParticipantIds().toArray());
                criteria.and(participantCriteria);
            }

            // PARTICIPANT ROLES
            if (request.getParticipantRoles() != null && !request.getParticipantRoles().isEmpty()) {
                Criteria roleCriteria = new Criteria("participants.role.keyword");
                roleCriteria.in(request.getParticipantRoles().toArray());
                criteria.and(roleCriteria);
            }

            // FLAG DESCRIPTION
            if (StringUtils.hasText(request.getFlagDescription())) {
                criteria.and(new Criteria("flagInfo.flagDescription").matches(request.getFlagDescription()));
            }

            // RULE ID
            if (StringUtils.hasText(request.getRuleId())) {
                criteria.and(new Criteria("flagInfo.ruleId.keyword").is(request.getRuleId()));
            }

            // TIME RANGES - Your timestamps are Long in ES, not Instant
            if (request.getStartTime() != null && request.getEndTime() != null) {
                criteria.and(new Criteria("timestamp")
                        .greaterThanEqual(request.getStartTime().toEpochMilli())
                        .lessThanEqual(request.getEndTime().toEpochMilli()));
            }

            // MESSAGE ID SEARCH
            if (StringUtils.hasText(request.getMessageId())) {
                criteria.and(new Criteria("messageId.keyword").is(request.getMessageId()));
            }

            CriteriaQuery query = new CriteriaQuery(criteria).setPageable(pageable);

            System.out.println("Final Criteria: " + criteria.toString());
            System.out.println("Executing query...");

            SearchHits<Message> searchHits = elasticsearchOperations.search(query, Message.class);

            System.out.println("Total hits found: " + searchHits.getTotalHits());

            List<Message> messages = searchHits.getSearchHits().stream()
                    .map(SearchHit::getContent)
                    .collect(Collectors.toList());

            System.out.println("Returning " + messages.size() + " messages");
            System.out.println("=== SEARCH COMPLETED ===");

            return new PageImpl<>(messages, pageable, searchHits.getTotalHits());

        } catch (Exception e) {
            System.err.println("ERROR in advancedSearch: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Search failed: " + e.getMessage(), e);
        }
    }
}
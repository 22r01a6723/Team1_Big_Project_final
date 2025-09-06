package com.Project1.Search.controller;

import com.Project1.Search.dto.SearchRequest;
import com.Project1.Search.model.Message;
import com.Project1.Search.service.MessageSearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;

@RestController
@RequestMapping("/api/search")
@RequiredArgsConstructor
public class SearchController {

    private final MessageSearchService searchService;

    @PostMapping
    public ResponseEntity<Page<Message>> search(@RequestBody SearchRequest request) {
        try {
            return ResponseEntity.ok(searchService.searchMessages(request));
        }
        catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }


    // working
    @GetMapping("/keyword/{tenantId}")
    public ResponseEntity<Page<Message>> fullTextSearch(
            @PathVariable String tenantId,
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(searchService.fullTextSearch(keyword, tenantId, page, size));
    }


    @GetMapping("/field/{tenantId}/{field}")
    public ResponseEntity<Page<Message>> searchByField(
            @PathVariable String tenantId,
            @PathVariable String field,
            @RequestParam String value,
            @RequestParam(defaultValue = "false") boolean exactMatch,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(searchService.searchByField(field, value, tenantId, exactMatch, page, size));
    }



    ///  working

    @GetMapping("/timerange/{tenantId}")
    public ResponseEntity<Page<Message>> searchByTimeRange(
            @PathVariable String tenantId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant end,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(searchService.searchByTimeRange(tenantId, start, end, page, size));
    }

    // working
    @GetMapping("/flagtimerange/{tenantId}")
    public ResponseEntity<Page<Message>> searchByFlagTimeRange(
            @PathVariable String tenantId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant end,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(searchService.searchByFlagTimeRange(tenantId, start, end, page, size));
    }

    //working
    @GetMapping("/network/{tenantId}/{network}")
    public ResponseEntity<Page<Message>> findByNetwork(
            @PathVariable String tenantId,
            @PathVariable String network,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(searchService.findByNetwork(tenantId, network, page, size));
    }

    //  working
    @GetMapping("/flagged/{tenantId}/{flagged}")
    public ResponseEntity<Page<Message>> findByFlaggedStatus(
            @PathVariable String tenantId,
            @PathVariable boolean flagged,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(searchService.findByFlaggedStatus(tenantId, flagged, page, size));
    }

    //working

    @GetMapping("/team/{tenantId}/{team}")
    public ResponseEntity<Page<Message>> findByTeam(
            @PathVariable String tenantId,
            @PathVariable String team,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(searchService.findByTeam(tenantId, team, page, size));
    }


    // working
    @GetMapping("/channel/{tenantId}/{channel}")
    public ResponseEntity<Page<Message>> findByChannel(
            @PathVariable String tenantId,
            @PathVariable String channel,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(searchService.findByChannel(tenantId, channel, page, size));
    }



    @GetMapping("/participant/{tenantId}/{participantId}")
    public ResponseEntity<Page<Message>> findByParticipantId(
            @PathVariable String tenantId,
            @PathVariable String participantId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(searchService.findByParticipantId(tenantId, participantId, page, size));
    }


//    @GetMapping("/role/{tenantId}/{role}")
//    public ResponseEntity<Page<Message>> findByParticipantRole(
//            @PathVariable String tenantId,
//            @PathVariable String role,
//            @RequestParam(defaultValue = "0") int page,
//            @RequestParam(defaultValue = "20") int size) {
//        return ResponseEntity.ok(searchService.findByParticipantRole(tenantId, role, page, size));
//    }

    // working
    @GetMapping("/flagdescription/{tenantId}")
    public ResponseEntity<Page<Message>> findByFlagDescription(
            @PathVariable String tenantId,
            @RequestParam String flagDescription,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(searchService.findByFlagDescription(tenantId, flagDescription, page, size));
    }

}
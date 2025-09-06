package com.Project_1.Review.Controller;

import com.Project_1.Review.entity.Flag;
import com.Project_1.Review.service.FlagService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/flags")
public class FlagController {
    private final FlagService service;

    public FlagController(FlagService service) {
        this.service = service;
    }

    //Fetch all flags for a specific tenant & network
    @GetMapping
    public List<Flag> getAllFlags(
            @RequestParam String tenantId,
            @RequestParam String network) {
        return service.getAllFlags(tenantId, network);
    }

    // Fetch messages for a tenant and channel with specific flag id
    @GetMapping("/{flagId}")
    public List<Flag> getAllFlagMessagesWithFlagId(
            @PathVariable String flagId,
            @RequestParam String tenantId,
            @RequestParam String network) {
        return service.getAllFlags(flagId, tenantId, network);
    }

    //Fetch flags for a given message, tenant, and network
    @GetMapping("/message")
    public List<Flag> getFlagsByMessageId(
            //@PathVariable String messageId,
            @RequestParam String tenantId,
            @RequestParam String network) {
        System.out.println(tenantId + network);
        //return service.getFlagsByMessageId(messageId, tenantId, network);
        return service.getFlagsByMessage(tenantId, network);
    }

    //Fetch flags for a given rule, tenant, and network
    @GetMapping("/rule/{ruleId}")
    public List<Flag> getFlagsByRuleId(
            @PathVariable String ruleId,
            @RequestParam String tenantId,
            @RequestParam String network) {
        return service.getFlagsByRuleId(ruleId, tenantId, network);
    }


    //Search flags by description (filtered by tenant & network)
    @GetMapping("/search")
    public List<Flag> searchFlagsByDescription(
            @RequestParam String description,
            @RequestParam String tenantId,
            @RequestParam String network) {
        return service.searchFlagsByDescription(description, tenantId, network);
    }

    //Fetch flags by created_at date range, tenant & network
    @GetMapping("/date-range")
    public List<Flag> getFlagsByDateRange(
            @RequestParam Long start,
            @RequestParam Long end,
            @RequestParam String tenantId,
            @RequestParam String network) {
        return service.getFlagsByDateRange(start, end, tenantId, network);
    }
}


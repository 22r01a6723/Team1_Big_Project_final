package com.project_1.normalizer.util.adapters;

import com.fasterxml.jackson.databind.JsonNode;
import com.project_1.normalizer.model.CanonicalMessage;

public interface MessageAdapter {
    boolean supports(String network);
    CanonicalMessage map(JsonNode root);
}

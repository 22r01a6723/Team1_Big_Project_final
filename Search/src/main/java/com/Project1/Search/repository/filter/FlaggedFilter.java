package com.Project1.Search.repository.filter;

import com.Project1.Search.dto.SearchRequest;
import org.springframework.data.elasticsearch.core.query.Criteria;
import org.springframework.stereotype.Component;

@Component
public class FlaggedFilter implements SearchFilter {
    @Override
    public void apply(SearchRequest request, Criteria rootCriteria) {
        if (request.getFlagged() != null) {
            rootCriteria.and(new Criteria("flagged").is(request.getFlagged()));
        }
    }
}

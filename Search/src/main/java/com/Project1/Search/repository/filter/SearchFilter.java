package com.Project1.Search.repository.filter;


import com.Project1.Search.dto.SearchRequest;
import org.springframework.data.elasticsearch.core.query.Criteria;

public interface SearchFilter {
    void apply(SearchRequest request, Criteria rootCriteria);
}

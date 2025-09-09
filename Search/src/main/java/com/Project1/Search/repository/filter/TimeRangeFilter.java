package com.Project1.Search.repository.filter;


import com.Project1.Search.dto.SearchRequest;
import org.springframework.data.elasticsearch.core.query.Criteria;
import org.springframework.stereotype.Component;

@Component
public class TimeRangeFilter implements SearchFilter {
    @Override
    public void apply(SearchRequest request, Criteria rootCriteria) {
        if (request.getStartTime() != null && request.getEndTime() != null) {
            rootCriteria.and(new Criteria("timestamp")
                    .greaterThanEqual(request.getStartTime())
                    .lessThanEqual(request.getEndTime()));
        }
    }
}

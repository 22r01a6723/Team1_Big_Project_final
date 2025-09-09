package com.Project1.Search.repository.filter;


import com.Project1.Search.dto.SearchRequest;
import org.springframework.data.elasticsearch.core.query.Criteria;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class FlagDescriptionFilter implements SearchFilter {
    @Override
    public void apply(SearchRequest request, Criteria rootCriteria) {
        if (StringUtils.hasText(request.getFlagDescription())) {
            rootCriteria.and(new Criteria("flagInfo.flagDescription").matches(request.getFlagDescription()));
        }
    }
}

package com.Project1.Search.repository.filter;


import com.Project1.Search.dto.SearchRequest;
import org.springframework.data.elasticsearch.core.query.Criteria;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class KeywordFilter implements SearchFilter {

    @Override
    public void apply(SearchRequest request, Criteria rootCriteria) {
        if (StringUtils.hasText(request.getKeyword())) {
            Criteria keywordCriteria = new Criteria();
            keywordCriteria.or(new Criteria("content.subject").matches(request.getKeyword()))
                    .or(new Criteria("content.body").matches(request.getKeyword()))
                    .or(new Criteria("participants.id").matches(request.getKeyword()))
                    .or(new Criteria("flagInfo.flagDescription").matches(request.getKeyword()))
                    .or(new Criteria("network").matches(request.getKeyword()));

            rootCriteria.and(keywordCriteria);
        }
    }
}

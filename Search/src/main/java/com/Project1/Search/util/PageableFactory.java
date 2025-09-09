package com.Project1.Search.util;


import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public class PageableFactory {

    private PageableFactory() {} // utility class

    public static Pageable create(int page, int size, String sortField) {
        return PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, sortField));
    }
}

package com.mos.backend.common.argumentresolvers.pageable;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;


public class CustomPageRequest extends PageRequest {

    protected CustomPageRequest(int pageNumber, int pageSize, Sort sort) {
        super(pageNumber, pageSize, sort);
    }

    @Override
    public int getPageNumber() {
        return super.getPageNumber() + 1;
    }
}

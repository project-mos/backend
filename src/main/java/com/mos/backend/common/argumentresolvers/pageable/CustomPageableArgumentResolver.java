package com.mos.backend.common.argumentresolvers.pageable;

import org.springframework.core.MethodParameter;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@Component
public class CustomPageableArgumentResolver implements HandlerMethodArgumentResolver {

    private static final int DEFAULT_PAGE_SIZE= 12;
    private static final String DEFAULT_SORT_FIELD = "createdAt";
    private static final String DEFAULT_SORT_DIRECTION = "DESC";

    private static final String PARAM_PAGE = "page";
    private static final String PARAM_SIZE = "size";
    private static final String PARAM_SORT = "sort";
    private static final String PARAM_DIRECTION = "direction";

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return Pageable.class.equals(parameter.getParameterType());
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        int page = getPage(webRequest);
        int size = getSize(webRequest);
        Sort sort = getSort(webRequest);

        if (page < 0) {
            page = 0;
        }
        return new CustomPageRequest(page , size, sort);
    }

    private int getPage(NativeWebRequest webRequest) {
        String page = webRequest.getParameter(PARAM_PAGE);
        return (page != null) ? Integer.parseInt(page) -1 : 0;
    }

    private int getSize(NativeWebRequest webRequest) {
        String size = webRequest.getParameter(PARAM_SIZE);
        return size != null ? Integer.parseInt(size) : DEFAULT_PAGE_SIZE;
    }

    private Sort getSort(NativeWebRequest webRequest) {
        String sortField = webRequest.getParameter(PARAM_SORT);
        if (sortField == null || sortField.trim().isEmpty()) {
            sortField = DEFAULT_SORT_FIELD;
        }

        String direction = webRequest.getParameter(PARAM_DIRECTION);
        if (direction == null || direction.trim().isEmpty()) {
            direction = DEFAULT_SORT_DIRECTION;
        }
        Sort.Direction sortDirection = Sort.Direction.fromString(direction);
        return Sort.by(sortDirection, sortField);
    }
}

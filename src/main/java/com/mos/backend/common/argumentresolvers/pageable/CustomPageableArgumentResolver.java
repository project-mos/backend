package com.mos.backend.common.argumentresolvers.pageable;

import org.springframework.core.MethodParameter;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.util.ArrayList;
import java.util.List;

@Component
public class CustomPageableArgumentResolver implements HandlerMethodArgumentResolver {

    private static final int DEFAULT_PAGE_SIZE= 12;
    private static final String DEFAULT_SORT_FIELD = "createdAt";
    private static final String DEFAULT_SORT_DIRECTION = "DESC";

    private static final String PARAM_PAGE = "page";
    private static final String PARAM_SIZE = "size";
    private static final String PARAM_SORT = "sort";

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
        String[] sortParams = webRequest.getParameterValues(PARAM_SORT);
        if (sortParams == null || sortParams.length == 0) {
            // 아무 sort 파라미터가 없으면 기본 정렬
            return Sort.by(Sort.Direction.DESC, DEFAULT_SORT_FIELD);
        }

        List<Sort.Order> orders = new ArrayList<>();
        for (String param : sortParams) {
            if (param == null || param.isBlank()) continue;
            // param 예시: "createdAt,desc"

            String[] split = param.split(",");
            String field = split[0];
            // 방향값이 없으면 DESC로 기본 처리
            String direction = (split.length > 1) ? split[1] : DEFAULT_SORT_DIRECTION;

            Sort.Direction sortDirection = Sort.Direction.fromString(direction);
            orders.add(new Sort.Order(sortDirection, field));
        }

        // 여러 Order 누적해서 Sort 생성
        return Sort.by(orders);
    }
}

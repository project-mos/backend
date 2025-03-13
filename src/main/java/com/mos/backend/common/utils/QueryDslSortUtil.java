package com.mos.backend.common.utils;

import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.PathBuilder;
import org.springframework.data.domain.Sort;

import java.util.ArrayList;
import java.util.List;

public class QueryDslSortUtil {
    public static List<OrderSpecifier<?>> toOrderSpecifiers(Sort sort, Class<?> entityClass) {
        PathBuilder<?> pathBuilder = new PathBuilder<>(entityClass, firstCharToLower(entityClass.getSimpleName()));
        List<OrderSpecifier<?>> orders = new ArrayList<>();
        for (Sort.Order order : sort) {
            String property = order.getProperty();  // 정렬할 프로퍼티명
            Order direction = order.isAscending() ? Order.ASC : Order.DESC;
            Expression<?> path = pathBuilder.get(property);
            orders.add(new OrderSpecifier<>(direction, (Expression) path));
        }
        return orders;
    }

    private static String firstCharToLower(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        return str.substring(0, 1).toLowerCase() + str.substring(1);
    }
}

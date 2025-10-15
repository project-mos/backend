package com.mos.backend.notifications.infrastructure.notificationlog;

import com.mos.backend.common.utils.QueryDslSortUtil;
import com.mos.backend.notifications.entity.NotificationLog;
import com.mos.backend.notifications.entity.NotificationReadStatus;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.mos.backend.notifications.entity.QNotificationLog.notificationLog;

@Repository
@RequiredArgsConstructor
public class NotificationLogQueryDslRepository {
    private final JPAQueryFactory queryFactory;

    public Page<NotificationLog> getNotificationLogs(Pageable pageable, Long userId, NotificationReadStatus readStatus) {
        List<NotificationLog> content = queryFactory
                .selectFrom(notificationLog)
                .where(
                        notificationLog.recipient.id.eq(userId),
                        readStatusEq(readStatus)
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(getOrderSpecifiers(pageable))
                .fetch();

        JPQLQuery<NotificationLog> countQuery = queryFactory
                .select(notificationLog)
                .from(notificationLog)
                .where(
                        notificationLog.recipient.id.eq(userId),
                        readStatusEq(readStatus)
                );
        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchCount);
    }


    private BooleanExpression readStatusEq(NotificationReadStatus readStatus) {
        return switch (readStatus) {
            case NotificationReadStatus.READ -> notificationLog.isRead.isTrue();
            case NotificationReadStatus.UNREAD -> notificationLog.isRead.isFalse();
            default -> null;
        };
    }

    private static OrderSpecifier[] getOrderSpecifiers(Pageable pageable) {
        return QueryDslSortUtil.toOrderSpecifiers(pageable.getSort(), NotificationLog.class).toArray(new OrderSpecifier[0]);
    }
}

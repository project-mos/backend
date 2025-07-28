package com.mos.backend.common.utils;

import com.mos.backend.common.exception.MosException;
import com.mos.backend.common.stomp.entity.StompErrorCode;
import com.mos.backend.common.stomp.entity.Subscription;
import com.mos.backend.common.stomp.entity.SubscriptionType;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StompHeaderUtil {
    private static final Pattern DESTINATION_PATTERN = Pattern.compile("^(/user)?/sub/([a-zA-Z\\-]+)(?:/(\\d+))?$", Pattern.CASE_INSENSITIVE);

    public static Subscription parseDestination(StompHeaderAccessor accessor) {
        String destination = accessor.getDestination();
        if (destination == null) {
            throw new MosException(StompErrorCode.INVALID_DESTINATION);
        }

        Matcher matcher = DESTINATION_PATTERN.matcher(destination);
        if (matcher.matches()) {
            String typeStr = matcher.group(2);
            Long id = matcher.group(3) != null ? Long.valueOf(matcher.group(3)) : null;

            SubscriptionType subscriptionType = SubscriptionType.from(typeStr);
            return Subscription.of(subscriptionType, id);
        }

        throw new MosException(StompErrorCode.INVALID_DESTINATION);

    }
}

package com.mos.backend.common.stomp.entity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Subscription {
    private SubscriptionType type;
    private Long id;

    public static Subscription of(SubscriptionType subscriptionType, Long chatRoomId) {
        Subscription subscription = new Subscription();
        subscription.type = subscriptionType;
        subscription.id = chatRoomId;
        return subscription;
    }
}

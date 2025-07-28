package com.mos.backend.common.stomp.entity;

import com.mos.backend.common.exception.MosException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum SubscriptionType {
    CHAT_ROOM_LIST("chat-rooms"),
    PRIVATE_CHAT_ROOM("private-chat-rooms"),
    STUDY_CHAT_ROOM("study-chat-rooms"),
    ;

    private final String value;

    public static SubscriptionType from(String value) {
        return java.util.Arrays.stream(values())
                .filter(type -> type.value.equals(value))
                .findFirst()
                .orElseThrow(() -> new MosException(StompErrorCode.INVALID_DESTINATION));
    }
}

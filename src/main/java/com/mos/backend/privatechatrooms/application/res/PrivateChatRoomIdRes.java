package com.mos.backend.privatechatrooms.application.res;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class PrivateChatRoomIdRes {
    private Long privateChatRoomId;

    public static PrivateChatRoomIdRes of(Long privateChatRoomId) {
        PrivateChatRoomIdRes res = new PrivateChatRoomIdRes();
        res.privateChatRoomId = privateChatRoomId;
        return res;
    }
}

package com.mos.backend.privatechatmessages.presentation.req;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PrivateChatMessagePublishReq {
    private Long privateChatRoomId;
    private String message;
}

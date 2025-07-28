package com.mos.backend.privatechatmessages.application.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PrivateChatMessageDto implements Serializable {
    private Long userId;
    private Long privateChatRoomId;
    private String message;
    private LocalDateTime sendTime;

    public static PrivateChatMessageDto of(Long userId, Long privateChatRoomId, String message, LocalDateTime sendTime) {
        PrivateChatMessageDto privateChatMessageDto = new PrivateChatMessageDto();
        privateChatMessageDto.userId = userId;
        privateChatMessageDto.privateChatRoomId = privateChatRoomId;
        privateChatMessageDto.message = message;
        privateChatMessageDto.sendTime = sendTime;
        return privateChatMessageDto;
    }
}

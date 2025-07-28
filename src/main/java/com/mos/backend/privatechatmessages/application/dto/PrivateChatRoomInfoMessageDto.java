package com.mos.backend.privatechatmessages.application.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class PrivateChatRoomInfoMessageDto {
    private Long toUserId;

    private Long privateChatRoomId;

    private String lastMessage;
    private LocalDateTime lastMessageAt;

    public static PrivateChatRoomInfoMessageDto of(Long toUserId, Long privateChatRoomId, String lastMessage, LocalDateTime lastMessageAt) {
        PrivateChatRoomInfoMessageDto dto = new PrivateChatRoomInfoMessageDto();
        dto.toUserId = toUserId;
        dto.privateChatRoomId = privateChatRoomId;
        dto.lastMessage = lastMessage;
        dto.lastMessageAt = lastMessageAt;
        return dto;
    }
}

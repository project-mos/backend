package com.mos.backend.studychatrooms.application.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class StudyChatRoomInfoMessageDto {
    private Long toUserId;

    private Long studyChatRoomId;

    private String lastMessage;
    private LocalDateTime lastMessageAt;

    public static StudyChatRoomInfoMessageDto of(Long toUserId, Long studyChatRoomId, String lastMessage, LocalDateTime lastMessageAt) {
        StudyChatRoomInfoMessageDto dto = new StudyChatRoomInfoMessageDto();
        dto.toUserId = toUserId;
        dto.studyChatRoomId = studyChatRoomId;
        dto.lastMessage = lastMessage;
        dto.lastMessageAt = lastMessageAt;
        return dto;
    }
}

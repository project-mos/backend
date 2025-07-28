package com.mos.backend.studychatrooms.application.res;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class MyStudyChatRoomRes {
    private Long studyChatRoomId;
    private String chatName;

    private String lastMessage;
    private LocalDateTime lastMessageAt;

    private int unreadCnt;

    public static MyStudyChatRoomRes of(Long studyChatRoomId, String chatName, String lastMessage, LocalDateTime lastMessageAt, int unreadCnt) {
        MyStudyChatRoomRes myStudyChatRoomRes = new MyStudyChatRoomRes();
        myStudyChatRoomRes.studyChatRoomId = studyChatRoomId;
        myStudyChatRoomRes.chatName = chatName;
        myStudyChatRoomRes.lastMessage = lastMessage;
        myStudyChatRoomRes.lastMessageAt = lastMessageAt;
        myStudyChatRoomRes.unreadCnt = unreadCnt;
        return myStudyChatRoomRes;
    }
}

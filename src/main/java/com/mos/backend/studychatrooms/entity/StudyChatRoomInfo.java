package com.mos.backend.studychatrooms.entity;

import com.mos.backend.studychatrooms.application.res.MyStudyChatRoomRes;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class StudyChatRoomInfo {
    private String chatName;

    private String lastMessage;
    private LocalDateTime lastMessageAt;

    private int unreadCnt;

    public static StudyChatRoomInfo of(MyStudyChatRoomRes myStudyChatRoomRes) {
        StudyChatRoomInfo info = new StudyChatRoomInfo();
        info.chatName = myStudyChatRoomRes.getChatName();
        info.lastMessage = myStudyChatRoomRes.getLastMessage();
        info.lastMessageAt = myStudyChatRoomRes.getLastMessageAt();
        info.unreadCnt = myStudyChatRoomRes.getUnreadCnt();
        return info;
    }

    public static StudyChatRoomInfo of(String chatName, String lastMessage, LocalDateTime lastMessageAt, int unreadCnt) {
        StudyChatRoomInfo info = new StudyChatRoomInfo();
        info.chatName = chatName;
        info.lastMessage = lastMessage;
        info.lastMessageAt = lastMessageAt;
        info.unreadCnt = unreadCnt;
        return info;
    }

    public void resetUnreadCnt() {
        this.unreadCnt = 0;
    }

    public void updateLastMessage(String lastMessage, LocalDateTime lastMessageAt) {
        this.lastMessage = lastMessage;
        this.lastMessageAt = lastMessageAt;
        this.unreadCnt++;
    }
}

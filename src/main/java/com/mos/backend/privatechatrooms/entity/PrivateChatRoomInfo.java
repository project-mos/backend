package com.mos.backend.privatechatrooms.entity;


import com.mos.backend.privatechatrooms.application.res.MyPrivateChatRoomRes;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PrivateChatRoomInfo implements Serializable {
    private String chatName;

    private String lastMessage;
    private LocalDateTime lastMessageAt;

    private int unreadCnt;

    public static PrivateChatRoomInfo of(MyPrivateChatRoomRes myPrivateChatRoomRes) {
        PrivateChatRoomInfo privateChatRoomInfo = new PrivateChatRoomInfo();

        privateChatRoomInfo.chatName = myPrivateChatRoomRes.getChatName();

        privateChatRoomInfo.lastMessage = myPrivateChatRoomRes.getLastMessage();
        privateChatRoomInfo.lastMessageAt = myPrivateChatRoomRes.getLastMessageAt();

        privateChatRoomInfo.unreadCnt = myPrivateChatRoomRes.getUnreadCnt();
        return privateChatRoomInfo;
    }

    public static PrivateChatRoomInfo of(String chatName, String lastMessage, LocalDateTime lastMessageAt, int unreadCnt) {
        PrivateChatRoomInfo privateChatRoomInfo = new PrivateChatRoomInfo();
        privateChatRoomInfo.chatName = chatName;
        privateChatRoomInfo.lastMessage = lastMessage;
        privateChatRoomInfo.lastMessageAt = lastMessageAt;
        privateChatRoomInfo.unreadCnt = unreadCnt;
        return privateChatRoomInfo;
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
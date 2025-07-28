package com.mos.backend.privatechatrooms.application.res;

import com.mos.backend.privatechatmessages.entity.PrivateChatMessage;
import com.mos.backend.privatechatrooms.entity.PrivateChatRoom;
import com.mos.backend.privatechatrooms.entity.PrivateChatRoomInfo;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class MyPrivateChatRoomRes {
    private Long privateChatRoomId;
    private String chatName;

    private String lastMessage;
    private LocalDateTime lastMessageAt;

    private int unreadCnt;

    public static MyPrivateChatRoomRes of(Long privateChatRoomId, String chatName, String lastMessage, LocalDateTime lastMessageAt, int unreadCnt) {
        MyPrivateChatRoomRes myPrivateChatRoomRes = new MyPrivateChatRoomRes();
        myPrivateChatRoomRes.privateChatRoomId = privateChatRoomId;
        myPrivateChatRoomRes.chatName = chatName;
        myPrivateChatRoomRes.lastMessage = lastMessage;
        myPrivateChatRoomRes.lastMessageAt = lastMessageAt;
        myPrivateChatRoomRes.unreadCnt = unreadCnt;
        return myPrivateChatRoomRes;
    }
}

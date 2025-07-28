package com.mos.backend.privatechatrooms.entity;

import com.mos.backend.common.entity.BaseAuditableEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "private_chat_rooms")
public class PrivateChatRoom extends BaseAuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "private_chat_room_id")
    private Long id;

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    private PrivateChatRoomStatus status;

    public static PrivateChatRoom createInvisibleChatRoom(String name) {
        PrivateChatRoom privateChatRoom = new PrivateChatRoom();
        privateChatRoom.status = PrivateChatRoomStatus.INVISIBLE;
        privateChatRoom.name = name;
        return privateChatRoom;
    }

    public void visible() {
        status = PrivateChatRoomStatus.VISIBLE;
    }
}

package com.mos.backend.privatechatroommember.entity;

import com.mos.backend.common.entity.BaseAuditableEntity;
import com.mos.backend.privatechatrooms.entity.PrivateChatRoom;
import com.mos.backend.users.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "private_chat_room_members")
public class PrivateChatRoomMember extends BaseAuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "private_chat_room_member_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false, name = "private_chat_room_id")
    private PrivateChatRoom privateChatRoom;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false, name = "user_id")
    private User user;

    private LocalDateTime lastEntryAt = LocalDateTime.now();

    public static PrivateChatRoomMember of(PrivateChatRoom privateChatRoom, User user) {
        PrivateChatRoomMember privateChatRoomMember = new PrivateChatRoomMember();
        privateChatRoomMember.privateChatRoom = privateChatRoom;
        privateChatRoomMember.user = user;
        privateChatRoomMember.lastEntryAt = LocalDateTime.now();
        return privateChatRoomMember;
    }

    public void updateLastEntryAt() {
        this.lastEntryAt = LocalDateTime.now();
    }
}
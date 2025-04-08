package com.mos.backend.privatechatrooms.infrastructure;

import com.mos.backend.privatechatrooms.entity.PrivateChatRoom;
import com.mos.backend.users.entity.User;

import java.util.List;
import java.util.Optional;

public interface PrivateChatRoomRepository {
    Optional<PrivateChatRoom> findById(Long id);

    PrivateChatRoom save(PrivateChatRoom privateChatRoom);

    Optional<Long> findPrivateChatRoomIdByUsers(User user1, User user2);

    List<PrivateChatRoom> findByRequesterOrReceiver(User user);
}

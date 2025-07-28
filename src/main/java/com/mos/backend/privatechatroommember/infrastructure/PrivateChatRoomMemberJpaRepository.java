package com.mos.backend.privatechatroommember.infrastructure;

import com.mos.backend.privatechatroommember.entity.PrivateChatRoomMember;
import com.mos.backend.privatechatrooms.entity.PrivateChatRoom;
import com.mos.backend.users.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PrivateChatRoomMemberJpaRepository extends JpaRepository<PrivateChatRoomMember, Long> {
    boolean existsByPrivateChatRoomAndUser(PrivateChatRoom privateChatRoom, User user);

    Optional<PrivateChatRoomMember> findByUserAndPrivateChatRoom(User user, PrivateChatRoom privateChatRoom);

    PrivateChatRoomMember findByUserIdAndPrivateChatRoomId(Long userId, Long privateChatRoomId);

    Optional<PrivateChatRoomMember> findByUserNotAndPrivateChatRoom(User user, PrivateChatRoom privateChatRoom);
}

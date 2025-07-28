package com.mos.backend.privatechatrooms.infrastructure;

import com.mos.backend.privatechatrooms.entity.PrivateChatRoom;
import com.mos.backend.users.entity.User;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface PrivateChatRoomJpaRepository extends JpaRepository<PrivateChatRoom, Long> {
    @Query("""
                select m.privateChatRoom.id
                from PrivateChatRoomMember m
                where m.user in (:user1, :user2)
                group by m.privateChatRoom.id
                having count(distinct m.user) = 2
            """)
    Optional<Long> findPrivateChatRoomIdByUsers(@Param("user1") User user1, @Param("user2") User user2);

    @Query("""
                select m.privateChatRoom
                from PrivateChatRoomMember m
                where m.user = :user
            """)
    List<PrivateChatRoom> findByUser(@Param("user") User user);
}

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
            SELECT p.id FROM PrivateChatRoom p 
            WHERE (p.requester = :user1 AND p.receiver = :user2) 
               OR (p.requester = :user2 AND p.receiver = :user1)
            """)
    Optional<Long> findPrivateChatRoomIdByUsers(@Param("user1") User user1, @Param("user2") User user2);

    @Query("""
            SELECT p FROM PrivateChatRoom p
            WHERE p.requester = :user OR p.receiver = :user
            """)
    List<PrivateChatRoom> findByRequesterOrReceiver(User user);
}

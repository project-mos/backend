package com.mos.backend.privatechatrooms.infrastructure;

import com.mos.backend.privatechatrooms.entity.PrivateChatRoom;
import com.mos.backend.users.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Repository
public class PrivateChatRoomRepositoryImpl implements PrivateChatRoomRepository {
    private final PrivateChatRoomJpaRepository privateChatRoomJpaRepository;

    @Override
    public Optional<PrivateChatRoom> findById(Long id) {
        return privateChatRoomJpaRepository.findById(id);
    }

    @Override
    public PrivateChatRoom save(PrivateChatRoom privateChatRoom) {
        return privateChatRoomJpaRepository.save(privateChatRoom);
    }

    @Override
    public Optional<Long> findPrivateChatRoomIdByUsers(User user1, User user2) {
        return privateChatRoomJpaRepository.findPrivateChatRoomIdByUsers(user1, user2);
    }

    @Override
    public List<PrivateChatRoom> findByRequesterOrReceiver(User user) {
        return privateChatRoomJpaRepository.findByRequesterOrReceiver(user);
    }
}

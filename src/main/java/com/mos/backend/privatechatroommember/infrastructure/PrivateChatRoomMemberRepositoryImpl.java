package com.mos.backend.privatechatroommember.infrastructure;

import com.mos.backend.privatechatroommember.entity.PrivateChatRoomMember;
import com.mos.backend.privatechatrooms.entity.PrivateChatRoom;
import com.mos.backend.users.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@RequiredArgsConstructor
@Repository
public class PrivateChatRoomMemberRepositoryImpl implements PrivateChatRoomMemberRepository {
    private final PrivateChatRoomMemberJpaRepository privateChatRoomMemberJpaRepository;

    @Override
    public boolean existsByPrivateChatRoomAndUser(PrivateChatRoom privateChatRoom, User user) {
        return privateChatRoomMemberJpaRepository.existsByPrivateChatRoomAndUser(privateChatRoom, user);
    }

    @Override
    public PrivateChatRoomMember save(PrivateChatRoomMember privateChatRoomMember) {
        return privateChatRoomMemberJpaRepository.save(privateChatRoomMember);
    }

    @Override
    public Optional<PrivateChatRoomMember> findByUserAndPrivateChatRoom(User user, PrivateChatRoom privateChatRoom) {
        return privateChatRoomMemberJpaRepository.findByUserAndPrivateChatRoom(user, privateChatRoom);
    }

    @Override
    public PrivateChatRoomMember findByUserIdAndPrivateChatRoomId(Long userId, Long privateChatRoomId) {
        return privateChatRoomMemberJpaRepository.findByUserIdAndPrivateChatRoomId(userId, privateChatRoomId);
    }

    @Override
    public Optional<PrivateChatRoomMember> findByUserNotAndPrivateChatRoom(User user, PrivateChatRoom privateChatRoom) {
        return privateChatRoomMemberJpaRepository.findByUserNotAndPrivateChatRoom(user, privateChatRoom);
    }
}

package com.mos.backend.privatechatrooms.application;

import com.mos.backend.common.exception.MosException;
import com.mos.backend.common.infrastructure.EntityFacade;
import com.mos.backend.common.redis.RedisPrivateChatRoomUtil;
import com.mos.backend.privatechatmessages.entity.PrivateChatMessage;
import com.mos.backend.privatechatmessages.infrastructure.PrivateChatMessageRepository;
import com.mos.backend.privatechatrooms.application.res.PrivateChatRoomRes;
import com.mos.backend.privatechatrooms.entity.PrivateChatRoom;
import com.mos.backend.privatechatrooms.entity.PrivateChatRoomErrorCode;
import com.mos.backend.privatechatrooms.infrastructure.PrivateChatRoomRepository;
import com.mos.backend.privatechatrooms.presentation.req.PrivateChatRoomCreateReq;
import com.mos.backend.users.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class PrivateChatRoomService {
    private final RedisPrivateChatRoomUtil redisPrivateChatRoomUtil;
    private final EntityFacade entityFacade;
    private final PrivateChatRoomRepository privateChatRoomRepository;
    private final PrivateChatMessageRepository privateChatMessageRepository;

    @Transactional
    public Long create(Long userId, PrivateChatRoomCreateReq req) {
        User requester = entityFacade.getUser(userId);
        User receiver = entityFacade.getUser(req.getReceiverId());

        privateChatRoomRepository.findPrivateChatRoomIdByUsers(requester, receiver)
                .ifPresent(privateChatRoomId -> {
                    throw new MosException(PrivateChatRoomErrorCode.CONFLICT);
                });

        PrivateChatRoom privateChatRoom = PrivateChatRoom.createInvisibleChatRoom(requester, receiver);
        return privateChatRoomRepository.save(privateChatRoom).getId();
    }

    @Transactional(readOnly = true)
    public Long getPrivateChatRoomId(Long userId, Long counterpartId) {
        User user1 = entityFacade.getUser(userId);
        User user2 = entityFacade.getUser(counterpartId);

        return privateChatRoomRepository.findPrivateChatRoomIdByUsers(user1, user2)
                .orElseThrow(() -> new MosException(PrivateChatRoomErrorCode.NOT_FOUND));
    }

    @Transactional(readOnly = true)
    public List<PrivateChatRoomRes> getMyPrivateChatRooms(Long userId) {
        User user = entityFacade.getUser(userId);
        List<PrivateChatRoom> privateChatRooms = privateChatRoomRepository.findByRequesterOrReceiver(user);

        List<PrivateChatRoomRes> privateChatRoomResList = privateChatRooms.stream()
                .map(privateChatRoom -> {
                    Long counterpartId = privateChatRoom.getCounterpart(user).getId();
                    Optional<PrivateChatMessage> optionalPrivateChatMessage = getLastChatMessage(privateChatRoom);

                    return optionalPrivateChatMessage
                            .map(privateChatMessage -> PrivateChatRoomRes.of(privateChatRoom, counterpartId, privateChatMessage))
                            .orElseGet(() -> PrivateChatRoomRes.of(privateChatRoom, counterpartId));
                })
                .toList();

        return privateChatRoomResList;
    }

    private Optional<PrivateChatMessage> getLastChatMessage(PrivateChatRoom privateChatRoom) {
        return privateChatMessageRepository.findFirstByPrivateChatRoomOrderByCreatedByDesc(privateChatRoom);
    }

    public void enter(Long userId, Long privateChatRoomId) {
        User user = entityFacade.getUser(userId);
        PrivateChatRoom privateChatRoom = entityFacade.getPrivateChatRoom(privateChatRoomId);
        redisPrivateChatRoomUtil.enterChatRoom(user.getId(), privateChatRoom.getId());
    }

    public void leave(Long userId, Long privateChatRoomId) {
        User user = entityFacade.getUser(userId);
        PrivateChatRoom privateChatRoom = entityFacade.getPrivateChatRoom(privateChatRoomId);
        redisPrivateChatRoomUtil.leaveChatRoom(user.getId(), privateChatRoom.getId());
    }
}

package com.mos.backend.privatechatmessages.application;

import com.mos.backend.common.dto.InfinityScrollRes;
import com.mos.backend.common.exception.MosException;
import com.mos.backend.common.infrastructure.EntityFacade;
import com.mos.backend.common.redis.RedisPublisher;
import com.mos.backend.common.utils.InfinityScrollUtil;
import com.mos.backend.privatechatmessages.application.dto.PrivateChatMessageDto;
import com.mos.backend.privatechatmessages.application.dto.PrivateChatRoomInfoMessageDto;
import com.mos.backend.privatechatmessages.application.res.PrivateChatMessageRes;
import com.mos.backend.privatechatmessages.entity.PrivateChatMessage;
import com.mos.backend.privatechatmessages.entity.PrivateChatMessageErrorCode;
import com.mos.backend.privatechatmessages.infrastructure.PrivateChatMessageRepository;
import com.mos.backend.privatechatmessages.presentation.req.PrivateChatMessagePublishReq;
import com.mos.backend.privatechatroommember.application.PrivateChatRoomMemberService;
import com.mos.backend.privatechatroommember.entity.PrivateChatRoomMember;
import com.mos.backend.privatechatrooms.entity.PrivateChatRoom;
import com.mos.backend.users.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class PrivateChatMessageService {

    private final PrivateChatMessageRepository privateChatMessageRepository;
    private final EntityFacade entityFacade;
    private final RedisPublisher redisPublisher;
    private final PrivateChatRoomMemberService privateChatRoomMemberService;

    @Transactional
    public void publish(Long userId, Long privateChatRoomId, PrivateChatMessagePublishReq req) {
        User user = entityFacade.getUser(userId);
        PrivateChatRoom privateChatRoom = entityFacade.getPrivateChatRoom(privateChatRoomId);

        privateChatRoom.visible();

        PrivateChatMessage privateChatMessage = savePrivateChatMessage(user, privateChatRoom, req.getMessage());
        redisPublisher.publishPrivateChatMessage(
                PrivateChatMessageDto.of(
                        user.getId(), user.getNickname(), privateChatRoom.getId(), privateChatMessage.getMessage(), privateChatMessage.getCreatedAt()
                )
        );

        PrivateChatRoomMember other = privateChatRoomMemberService.findByUserNotAndPrivateChatRoom(user, privateChatRoom);
        PrivateChatRoomInfoMessageDto privateChatRoomInfoMessageDto = PrivateChatRoomInfoMessageDto.of(
                other.getUser().getId(), privateChatRoom.getId(), privateChatMessage.getMessage(), privateChatMessage.getCreatedAt()
        );
        redisPublisher.publishPrivateChatRoomInfoMessage(privateChatRoomInfoMessageDto);
    }

    private PrivateChatMessage savePrivateChatMessage(User user, PrivateChatRoom privateChatRoom, String message) {
        PrivateChatMessage privateChatMessage = PrivateChatMessage.of(user, privateChatRoom, message);
        return privateChatMessageRepository.save(privateChatMessage);
    }

    @PreAuthorize("@chatRoomSecurity.isPrivateChatRoomMember(#userId, #privateChatRoomId)")
    @Transactional(readOnly = true)
    public InfinityScrollRes<PrivateChatMessageRes> getPrivateChatMessages(Long userId, Long privateChatRoomId, Long lastPrivateChatMessageId, int size) {
        PrivateChatRoom privateChatRoom = entityFacade.getPrivateChatRoom(privateChatRoomId);

        List<PrivateChatMessage> privateChatMessages = privateChatMessageRepository.findAllByChatRoomIdForInfiniteScroll(
                privateChatRoom.getId(), lastPrivateChatMessageId, size
        );

        boolean hasNext = InfinityScrollUtil.hasNext(privateChatMessages, size);
        if (hasNext)
            privateChatMessages.remove(privateChatMessages.size() - 1);

        Optional<PrivateChatMessage> lastElement = InfinityScrollUtil.getLastElement(privateChatMessages);
        Long lastElementId = lastElement.map(PrivateChatMessage::getId).orElse(null);

        List<PrivateChatMessageRes> privateChatMessageResList = privateChatMessages.stream()
                .map(PrivateChatMessageRes::of)
                .toList();

        return InfinityScrollRes.of(privateChatMessageResList, lastElementId, hasNext);
    }

    @Transactional(readOnly = true)
    public int getUnreadCnt(Long userId, Long privateChatRoomId) {
        User user = entityFacade.getUser(userId);
        PrivateChatRoom privateChatRoom = entityFacade.getPrivateChatRoom(privateChatRoomId);

        PrivateChatRoomMember privateChatRoomMember = privateChatRoomMemberService.findByUserAndPrivateChatRoom(user, privateChatRoom);
        LocalDateTime lastEntryAt = privateChatRoomMember.getLastEntryAt();

        return privateChatMessageRepository.countByPrivateChatRoomIdAndCreatedAtAfter(privateChatRoom.getId(), lastEntryAt);
    }

    public PrivateChatMessage getLastMessage(PrivateChatRoom privateChatRoom) {
        return privateChatMessageRepository.findFirstByPrivateChatRoomOrderByCreatedAtDesc(privateChatRoom)
                .orElseThrow(() -> new MosException(PrivateChatMessageErrorCode.NOT_FOUND));
    }
}

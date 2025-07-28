package com.mos.backend.privatechatrooms.application;

import com.mos.backend.common.exception.MosException;
import com.mos.backend.common.infrastructure.EntityFacade;
import com.mos.backend.common.redis.RedisPublisher;
import com.mos.backend.privatechatmessages.application.PrivateChatMessageService;
import com.mos.backend.privatechatmessages.application.dto.PrivateChatRoomInfoMessageDto;
import com.mos.backend.privatechatmessages.entity.PrivateChatMessage;
import com.mos.backend.privatechatrooms.application.res.MyPrivateChatRoomRes;
import com.mos.backend.privatechatrooms.entity.PrivateChatRoom;
import com.mos.backend.privatechatrooms.entity.PrivateChatRoomErrorCode;
import com.mos.backend.privatechatrooms.entity.PrivateChatRoomInfo;
import com.mos.backend.privatechatrooms.infrastructure.PrivateChatRoomInfoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@Service
public class PrivateChatRoomInfoService {

    private final EntityFacade entityFacade;
    private final PrivateChatMessageService privateChatMessageService;
    private final PrivateChatRoomInfoRepository privateChatRoomInfoRepository;
    private final RedisPublisher redisPublisher;

    public void cachingPrivateChatRoomInfos(Long userId, List<MyPrivateChatRoomRes> myPrivateChatRoomResList) {
        myPrivateChatRoomResList.forEach(myPrivateChatRoomRes -> {
            Long privateChatRoomId = myPrivateChatRoomRes.getPrivateChatRoomId();
            PrivateChatRoomInfo info = PrivateChatRoomInfo.of(myPrivateChatRoomRes);
            privateChatRoomInfoRepository.save(userId, privateChatRoomId, info);
        });
    }

    public void resetUnreadCount(Long userId, Long privateChatRoomId) {
        PrivateChatRoomInfo info = privateChatRoomInfoRepository.resetUnreadCount(userId, privateChatRoomId);

        PrivateChatRoomInfoMessageDto privateChatMessageDto = PrivateChatRoomInfoMessageDto.of(
                userId, privateChatRoomId, info.getLastMessage(), info.getLastMessageAt()
        );

        redisPublisher.publishPrivateChatRoomInfoMessage(privateChatMessageDto);
    }

    public MyPrivateChatRoomRes updatePrivateChatRoomInfo(PrivateChatRoomInfoMessageDto dto) {
        Long userId = dto.getToUserId();
        Long privateChatRoomId = dto.getPrivateChatRoomId();
        String lastMessage = dto.getLastMessage();
        LocalDateTime lastMessageAt = dto.getLastMessageAt();

        try {
            PrivateChatRoomInfo info = privateChatRoomInfoRepository.updateChatRoomInfo(userId, privateChatRoomId, lastMessage, lastMessageAt);
            return MyPrivateChatRoomRes.of(
                    privateChatRoomId, info.getChatName(), info.getLastMessage(), info.getLastMessageAt(), info.getUnreadCnt()
            );
        } catch (MosException e) {
            if (e.getErrorCode() != PrivateChatRoomErrorCode.INFO_NOT_FOUND) {
                PrivateChatRoomInfo info = savePrivateChatRoomInfo(privateChatRoomId, userId);
                return MyPrivateChatRoomRes.of(
                        privateChatRoomId, info.getChatName(), info.getLastMessage(), info.getLastMessageAt(), info.getUnreadCnt()
                );
            }

            throw e;
        }
    }

    public PrivateChatRoomInfo savePrivateChatRoomInfo(Long privateChatRoomId, Long userId) {
        PrivateChatRoom privateChatRoom = entityFacade.getPrivateChatRoom(privateChatRoomId);
        PrivateChatMessage privateChatMessage = privateChatMessageService.getLastMessage(privateChatRoom);
        int unreadCount = privateChatMessageService.getUnreadCnt(userId, privateChatRoom.getId());

        PrivateChatRoomInfo info = PrivateChatRoomInfo.of(
                privateChatRoom.getName(), privateChatMessage.getMessage(), privateChatMessage.getCreatedAt(), unreadCount
        );
        privateChatRoomInfoRepository.save(userId, privateChatRoomId, info);

        return info;
    }

    public void resetChatRoomInfos(Long userId) {
        privateChatRoomInfoRepository.resetChatRoomInfos(userId);
    }
}

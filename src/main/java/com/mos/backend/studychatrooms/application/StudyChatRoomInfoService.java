package com.mos.backend.studychatrooms.application;

import com.mos.backend.common.exception.MosException;
import com.mos.backend.common.infrastructure.EntityFacade;
import com.mos.backend.common.redis.RedisPublisher;
import com.mos.backend.studychatmessages.application.StudyChatMessageService;
import com.mos.backend.studychatmessages.entity.StudyChatMessage;
import com.mos.backend.studychatrooms.application.dto.StudyChatRoomInfoMessageDto;
import com.mos.backend.studychatrooms.application.res.MyStudyChatRoomRes;
import com.mos.backend.studychatrooms.entity.StudyChatRoom;
import com.mos.backend.studychatrooms.entity.StudyChatRoomErrorCode;
import com.mos.backend.studychatrooms.entity.StudyChatRoomInfo;
import com.mos.backend.studychatrooms.infrastructure.StudyChatRoomInfoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@Service
public class StudyChatRoomInfoService {

    private final EntityFacade entityFacade;
    private final StudyChatMessageService studyChatMessageService;
    private final StudyChatRoomInfoRepository studyChatRoomInfoRepository;
    private final RedisPublisher redisPublisher;

    public void cachingStudyChatRoomInfos(Long userId, List<MyStudyChatRoomRes> myStudyChatRoomResList) {
        myStudyChatRoomResList.forEach(myStudyChatRoomRes -> {
            Long studyChatRoomId = myStudyChatRoomRes.getStudyChatRoomId();
            StudyChatRoomInfo info = StudyChatRoomInfo.of(myStudyChatRoomRes);
            studyChatRoomInfoRepository.save(userId, studyChatRoomId, info);
        });
    }

    public void resetUnreadCount(Long userId, Long studyChatRoomId) {
        StudyChatRoomInfo info = studyChatRoomInfoRepository.resetUnreadCount(userId, studyChatRoomId);

        StudyChatRoomInfoMessageDto messageDto = StudyChatRoomInfoMessageDto.of(
                userId, studyChatRoomId, info.getLastMessage(), info.getLastMessageAt()
        );

        redisPublisher.publishStudyChatRoomInfoMessage(messageDto);
    }

    public MyStudyChatRoomRes updateStudyChatRoomInfo(StudyChatRoomInfoMessageDto dto) {
        Long userId = dto.getToUserId();
        Long studyChatRoomId = dto.getStudyChatRoomId();
        String lastMessage = dto.getLastMessage();
        LocalDateTime lastMessageAt = dto.getLastMessageAt();

        try {
            StudyChatRoomInfo info = studyChatRoomInfoRepository.updateChatRoomInfo(userId, studyChatRoomId, lastMessage, lastMessageAt);
            return MyStudyChatRoomRes.of(
                    studyChatRoomId, info.getChatName(), info.getLastMessage(), info.getLastMessageAt(), info.getUnreadCnt()
            );
        } catch (MosException e) {
            if (e.getErrorCode() != StudyChatRoomErrorCode.INFO_NOT_FOUND) {
                StudyChatRoomInfo info = saveStudyChatRoomInfo(studyChatRoomId, userId);
                return MyStudyChatRoomRes.of(
                        studyChatRoomId, info.getChatName(), info.getLastMessage(), info.getLastMessageAt(), info.getUnreadCnt()
                );
            }

            throw e;
        }
    }

    public StudyChatRoomInfo saveStudyChatRoomInfo(Long studyChatRoomId, Long userId) {
        StudyChatRoom studyChatRoom = entityFacade.getStudyChatRoom(studyChatRoomId);
        StudyChatMessage studyChatMessage = studyChatMessageService.getLastMessage(studyChatRoom);
        int unreadCount = studyChatMessageService.getUnreadCnt(userId, studyChatRoom.getId());

        StudyChatRoomInfo info = StudyChatRoomInfo.of(
                studyChatRoom.getName(), studyChatMessage.getMessage(), studyChatMessage.getCreatedAt(), unreadCount
        );
        studyChatRoomInfoRepository.save(userId, studyChatRoomId, info);

        return info;
    }

    public void resetChatRoomInfos(Long userId) {
        studyChatRoomInfoRepository.resetChatRoomInfos(userId);
    }
}

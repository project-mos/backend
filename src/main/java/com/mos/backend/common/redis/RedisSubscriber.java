package com.mos.backend.common.redis;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mos.backend.common.exception.MosException;
import com.mos.backend.privatechatmessages.application.dto.PrivateChatMessageDto;
import com.mos.backend.privatechatmessages.application.dto.PrivateChatRoomInfoMessageDto;
import com.mos.backend.privatechatmessages.entity.PrivateChatMessageErrorCode;
import com.mos.backend.privatechatrooms.application.PrivateChatRoomInfoService;
import com.mos.backend.privatechatrooms.application.res.MyPrivateChatRoomRes;
import com.mos.backend.studychatmessages.application.dto.StudyChatMessageDto;
import com.mos.backend.studychatmessages.entity.exception.StudyChatMessageErrorCode;
import com.mos.backend.studychatrooms.application.StudyChatRoomInfoService;
import com.mos.backend.studychatrooms.application.dto.StudyChatRoomInfoMessageDto;
import com.mos.backend.studychatrooms.application.res.MyStudyChatRoomRes;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class RedisSubscriber {

    private final ObjectMapper objectMapper;
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final PrivateChatRoomInfoService privateChatRoomInfoService;
    private final StudyChatRoomInfoService studyChatRoomInfoService;

    private final String USER_CHAT_ROOMS_DESTINATION = "/sub/chat-rooms";
    private final String PRIVATE_CHAT_FORMAT = "/sub/private-chat-rooms/%d";
    private final String STUDY_CHAT_FORMAT = "/sub/study-chat-rooms/%d";

    public void onPrivateChatMessage(String publishedMessage) {
        try {
            PrivateChatMessageDto privateChatMessageDto = objectMapper.readValue(publishedMessage, PrivateChatMessageDto.class);
            String destinationPath = generatePrivateDestinationPath(privateChatMessageDto.getPrivateChatRoomId());
            simpMessagingTemplate.convertAndSend(destinationPath, privateChatMessageDto);
        } catch (JsonProcessingException e) {
            throw new MosException(PrivateChatMessageErrorCode.DESERIALIZATION_FAILED);
        }
    }

    public void onStudyChatMessage(String publishedMessage) {
        try {
            StudyChatMessageDto studyChatMessageDto = objectMapper.readValue(publishedMessage, StudyChatMessageDto.class);
            String destinationPath = generateStudyDestinationPath(studyChatMessageDto.getStudyChatRoomId());
            simpMessagingTemplate.convertAndSend(destinationPath, studyChatMessageDto);
        } catch (JsonProcessingException e) {
            throw new MosException(StudyChatMessageErrorCode.DESERIALIZATION_FAILED);
        }
    }

    public void onPrivateChatRoomInfoMessage(String publishedMessage) {
        try {
            PrivateChatRoomInfoMessageDto privateChatRoomInfoMessageDto = objectMapper.readValue(publishedMessage, PrivateChatRoomInfoMessageDto.class);
            String userId = privateChatRoomInfoMessageDto.getToUserId().toString();
            MyPrivateChatRoomRes res = privateChatRoomInfoService.updatePrivateChatRoomInfo(privateChatRoomInfoMessageDto);

            simpMessagingTemplate.convertAndSendToUser(userId, USER_CHAT_ROOMS_DESTINATION, res);
        } catch (Exception e) {
            throw new MosException(PrivateChatMessageErrorCode.DESERIALIZATION_FAILED);
        }
    }

    public void onStudyChatRoomInfoMessage(String publishedMessage) {
        try {
            StudyChatRoomInfoMessageDto studyChatRoomInfoMessageDto = objectMapper.readValue(publishedMessage, StudyChatRoomInfoMessageDto.class);
            String userId = studyChatRoomInfoMessageDto.getToUserId().toString();
            MyStudyChatRoomRes res = studyChatRoomInfoService.updateStudyChatRoomInfo(studyChatRoomInfoMessageDto);

            simpMessagingTemplate.convertAndSendToUser(userId, USER_CHAT_ROOMS_DESTINATION, res);
        } catch (Exception e) {
            throw new MosException(StudyChatMessageErrorCode.DESERIALIZATION_FAILED);
        }
    }

    private String generatePrivateDestinationPath(Long privateChatRoomId) {
        return PRIVATE_CHAT_FORMAT.formatted(privateChatRoomId);
    }

    private String generateStudyDestinationPath(Long studyChatRoomId) {
        return STUDY_CHAT_FORMAT.formatted(studyChatRoomId);
    }
}

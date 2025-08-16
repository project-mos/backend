package com.mos.backend.studychatmessages.presentation.controller.api;

import com.mos.backend.common.dto.InfinityScrollRes;
import com.mos.backend.common.utils.StompPrincipalUtil;
import com.mos.backend.studychatmessages.application.StudyChatMessageService;
import com.mos.backend.studychatmessages.application.res.StudyChatMessageRes;
import com.mos.backend.studychatmessages.presentation.req.StudyChatMessagePublishReq;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class StudyChatMessageController {
    private final StudyChatMessageService studyChatMessageService;

    @MessageMapping("/studies/chat-rooms/{studyChatRoomId}/messages")
    public void publishPrivateChatMessage(Message<?> message,
                                          @DestinationVariable Long studyChatRoomId,
                                          @Validated StudyChatMessagePublishReq studyChatMessagePublishReq) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        Long userId = StompPrincipalUtil.getUserId(accessor);
        studyChatMessageService.publish(userId, studyChatRoomId, studyChatMessagePublishReq);
    }

    @GetMapping("/studies/{studyId}/chat-rooms/{studyChatRoomId}/messages")
    public InfinityScrollRes<StudyChatMessageRes> getStudyChatMessages(@PathVariable Long studyId,
                                                                       @PathVariable Long studyChatRoomId,
                                                                       @RequestParam(required = false) Long lastStudyChatMessageId,
                                                                       @RequestParam(defaultValue = "10") Integer size) {
        return studyChatMessageService.getStudyChatMessages(studyId, studyChatRoomId, lastStudyChatMessageId, size);
    }
}

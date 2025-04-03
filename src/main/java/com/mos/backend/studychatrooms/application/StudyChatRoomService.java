package com.mos.backend.studychatrooms.application;

import com.mos.backend.common.infrastructure.EntityFacade;
import com.mos.backend.common.redis.RedisPrivateChatRoomUtil;
import com.mos.backend.common.redis.RedisStudyChatRoomUtil;
import com.mos.backend.privatechatrooms.entity.PrivateChatRoom;
import com.mos.backend.studychatrooms.entity.StudyChatRoom;
import com.mos.backend.users.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class StudyChatRoomService {
    private final RedisStudyChatRoomUtil redisStudyChatRoomUtil;
    private final EntityFacade entityFacade;

    public void enter(Long userId, Long studyChatRoomId) {
        User user = entityFacade.getUser(userId);
        StudyChatRoom studyChatRoom = entityFacade.getStudyChatRoom(studyChatRoomId);
        redisStudyChatRoomUtil.enterChatRoom(user.getId(), studyChatRoom.getId());
    }

    public void leave(Long userId, Long studyChatRoomId) {
        User user = entityFacade.getUser(userId);
        StudyChatRoom studyChatRoom = entityFacade.getStudyChatRoom(studyChatRoomId);
        redisStudyChatRoomUtil.leaveChatRoom(user.getId(), studyChatRoom.getId());
    }
}

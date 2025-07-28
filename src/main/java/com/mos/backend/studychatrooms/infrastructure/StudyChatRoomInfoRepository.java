package com.mos.backend.studychatrooms.infrastructure;

import com.mos.backend.common.exception.MosException;
import com.mos.backend.studychatrooms.entity.StudyChatRoomErrorCode;
import com.mos.backend.studychatrooms.entity.StudyChatRoomInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@Repository
public class StudyChatRoomInfoRepository {
    private final RedisTemplate<String, StudyChatRoomInfo> redisTemplate;
    private static final String KEY_FORMAT = "USER::%d::STUDY-CHAT-ROOM";

    public void save(Long userId, Long studyChatRoomId, StudyChatRoomInfo info) {
        String key = KEY_FORMAT.formatted(userId);
        HashOperations<String, Long, StudyChatRoomInfo> operations = redisTemplate.opsForHash();
        operations.put(key, studyChatRoomId, info);
    }

    public StudyChatRoomInfo resetUnreadCount(Long userId, Long studyChatRoomId) {
        String key = KEY_FORMAT.formatted(userId);
        HashOperations<String, Long, StudyChatRoomInfo> operations = redisTemplate.opsForHash();
        StudyChatRoomInfo info = operations.get(key, studyChatRoomId);
        if (info != null) {
            info.resetUnreadCnt();
            operations.put(key, studyChatRoomId, info);
        }

        return info;
    }

    public StudyChatRoomInfo updateChatRoomInfo(Long userId, Long studyChatRoomId, String lastMessage, LocalDateTime lastMessageAt) {
        String key = KEY_FORMAT.formatted(userId);
        HashOperations<String, Long, StudyChatRoomInfo> operations = redisTemplate.opsForHash();

        StudyChatRoomInfo info = operations.get(key, studyChatRoomId);
        if (info == null)
            throw new MosException(StudyChatRoomErrorCode.INFO_NOT_FOUND);

        info.updateLastMessage(lastMessage, lastMessageAt);
        operations.put(key, studyChatRoomId, info);

        return info;
    }

    public void resetChatRoomInfos(Long userId) {
        String key = KEY_FORMAT.formatted(userId);
        redisTemplate.delete(key);
    }
}

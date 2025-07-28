package com.mos.backend.privatechatrooms.infrastructure;

import com.mos.backend.common.exception.MosException;
import com.mos.backend.privatechatrooms.entity.PrivateChatRoomErrorCode;
import com.mos.backend.privatechatrooms.entity.PrivateChatRoomInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@Repository
public class PrivateChatRoomInfoRepository {
    private final RedisTemplate<String, PrivateChatRoomInfo> redisTemplate;
    private static final String KEY_FORMAT = "USER::%d::PRIVATE-CHAT-ROOM";

    public void save(Long userId, Long privateChatRoomId, PrivateChatRoomInfo info) {
        String key = KEY_FORMAT.formatted(userId);
        HashOperations<String, Long, PrivateChatRoomInfo> operations = redisTemplate.opsForHash();
        operations.put(key, privateChatRoomId, info);
    }

    public PrivateChatRoomInfo resetUnreadCount(Long userId, Long privateChatRoomId) {
        String key = KEY_FORMAT.formatted(userId);
        HashOperations<String, Long, PrivateChatRoomInfo> operations = redisTemplate.opsForHash();
        PrivateChatRoomInfo info = operations.get(key, privateChatRoomId);
        if (info != null) {
            info.resetUnreadCnt();
            operations.put(key, privateChatRoomId, info);
        }

        return info;
    }

    public PrivateChatRoomInfo updateChatRoomInfo(Long userId, Long privateChatRoomId, String lastMessage, LocalDateTime lastMessageAt) {
        String key = KEY_FORMAT.formatted(userId);
        HashOperations<String, Long, PrivateChatRoomInfo> operations = redisTemplate.opsForHash();

        PrivateChatRoomInfo info = operations.get(key, privateChatRoomId);
        if (info == null)
            throw new MosException(PrivateChatRoomErrorCode.INFO_NOT_FOUND);

        info.updateLastMessage(lastMessage, lastMessageAt);
        operations.put(key, privateChatRoomId, info);

        return info;
    }

    public void resetChatRoomInfos(Long userId) {
        String key = KEY_FORMAT.formatted(userId);
        redisTemplate.delete(key);
    }
}

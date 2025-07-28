package com.mos.backend.privatechatmessages.infrastructure;

import com.mos.backend.privatechatmessages.entity.PrivateChatMessage;
import com.mos.backend.privatechatrooms.entity.PrivateChatRoom;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface PrivateChatMessageRepository {
    PrivateChatMessage save(PrivateChatMessage privateChatMessage);

    Optional<PrivateChatMessage> findFirstByPrivateChatRoomOrderByCreatedAtDesc(PrivateChatRoom privateChatRoom);

    List<PrivateChatMessage> findAllByChatRoomIdForInfiniteScroll(Long privateChatRoomId, Long lastPrivateChatMessageId, int size);

    int countByPrivateChatRoomIdAndCreatedAtAfter(Long privateChatRoomId, LocalDateTime lastEntryAt);
}

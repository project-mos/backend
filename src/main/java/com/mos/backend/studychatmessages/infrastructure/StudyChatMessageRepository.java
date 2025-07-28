package com.mos.backend.studychatmessages.infrastructure;

import com.mos.backend.studychatmessages.entity.StudyChatMessage;
import com.mos.backend.studychatrooms.entity.StudyChatRoom;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface StudyChatMessageRepository {
    StudyChatMessage save(StudyChatMessage studyChatMessage);

    List<StudyChatMessage> findAllByChatRoomIdForInfiniteScroll(Long studyChatRoomId, Long lastStudyChatMessageId, Integer size);

    int countByStudyChatRoomIdAndCreatedAtAfter(Long studyChatRoomId, LocalDateTime lastEntryAt);

    Optional<StudyChatMessage> findFirstByStudyChatRoomOrderByCreatedAtDesc(StudyChatRoom studyChatRoom);
}

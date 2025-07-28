package com.mos.backend.studychatmessages.infrastructure;

import com.mos.backend.studychatmessages.entity.StudyChatMessage;
import com.mos.backend.studychatrooms.entity.StudyChatRoom;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Repository
public class StudyChatMessageRepositoryImpl implements StudyChatMessageRepository {
    private final StudyChatMessageJpaRepository studyChatMessageJpaRepository;
    private final StudyChatMessageQueryDslRepository studyChatMessageQueryDslRepository;

    @Override
    public StudyChatMessage save(StudyChatMessage studyChatMessage) {
        return studyChatMessageJpaRepository.save(studyChatMessage);
    }

    @Override
    public List<StudyChatMessage> findAllByChatRoomIdForInfiniteScroll(Long studyChatRoomId, Long lastStudyChatMessageId, Integer size) {
        return studyChatMessageQueryDslRepository.findAllByChatRoomIdForInfiniteScroll(studyChatRoomId, lastStudyChatMessageId, size);
    }

    @Override
    public int countByStudyChatRoomIdAndCreatedAtAfter(Long studyChatRoomId, LocalDateTime lastEntryAt) {
        return studyChatMessageJpaRepository.countByStudyChatRoomIdAndCreatedAtAfter(studyChatRoomId, lastEntryAt);
    }

    @Override
    public Optional<StudyChatMessage> findFirstByStudyChatRoomOrderByCreatedAtDesc(StudyChatRoom studyChatRoom) {
        return studyChatMessageJpaRepository.findFirstByStudyChatRoomOrderByCreatedAtDesc(studyChatRoom);
    }
}

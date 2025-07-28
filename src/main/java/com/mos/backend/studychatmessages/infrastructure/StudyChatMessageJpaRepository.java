package com.mos.backend.studychatmessages.infrastructure;

import com.mos.backend.studychatmessages.entity.StudyChatMessage;
import com.mos.backend.studychatrooms.entity.StudyChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;

public interface StudyChatMessageJpaRepository extends JpaRepository<StudyChatMessage, Long> {
    int countByStudyChatRoomIdAndCreatedAtAfter(Long studyChatRoomId, LocalDateTime lastEntryAt);

    Optional<StudyChatMessage> findFirstByStudyChatRoomOrderByCreatedAtDesc(StudyChatRoom studyChatRoom);
}

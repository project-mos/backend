package com.mos.backend.studychatrooms.infrastructure;

import com.mos.backend.studychatrooms.entity.StudyChatRoom;

import java.util.List;
import java.util.Optional;

public interface StudyChatRoomRepository {
    Optional<StudyChatRoom> findById(Long id);

    StudyChatRoom save(StudyChatRoom studyChatRoom);

    List<StudyChatRoom> findAllByUserId(Long userId);
}

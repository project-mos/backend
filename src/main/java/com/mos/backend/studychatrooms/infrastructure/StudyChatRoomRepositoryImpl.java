package com.mos.backend.studychatrooms.infrastructure;

import com.mos.backend.studychatrooms.entity.StudyChatRoom;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Repository
public class StudyChatRoomRepositoryImpl implements StudyChatRoomRepository {
    private final StudyChatRoomJpaRepository studyChatRoomJpaRepository;

    @Override
    public Optional<StudyChatRoom> findById(Long id) {
        return studyChatRoomJpaRepository.findById(id);
    }

    @Override
    public StudyChatRoom save(StudyChatRoom studyChatRoom) {
        return studyChatRoomJpaRepository.save(studyChatRoom);
    }

    @Override
    public List<StudyChatRoom> findAllByUserId(Long userId) {
        return studyChatRoomJpaRepository.findAllByUserId(userId);
    }
}

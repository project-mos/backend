package com.mos.backend.studychatrooms.infrastructure;

import com.mos.backend.studychatrooms.entity.StudyChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface StudyChatRoomJpaRepository extends JpaRepository<StudyChatRoom, Long> {

    @Query("""
                select scr
                from StudyChatRoom scr
                where scr.study.id in (
                    select sm.study.id
                    from StudyMember sm
                    where sm.user.id = :userId
                ) and scr.status = 'VISIBLE'
            """)
    List<StudyChatRoom> findAllByUserId(Long userId);
}

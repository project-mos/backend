package com.mos.backend.studyjoins.infrastructure;

import com.mos.backend.studyjoins.entity.StudyJoin;

import java.util.List;
import java.util.Optional;

public interface StudyJoinRepository {
    StudyJoin save(StudyJoin studyJoin);

    Optional<StudyJoin> findById(Long studyApplicationId);

    List<StudyJoin> findAllByUserIdAndStatus(Long userId, String StudyJoinStatusCond);

    boolean existsByUserIdAndStudyId(Long userId, Long studyId);

    List<StudyJoin> findAllByStudyIdAndStatus(Long studyId, String StudyJoinStatusCond);
}

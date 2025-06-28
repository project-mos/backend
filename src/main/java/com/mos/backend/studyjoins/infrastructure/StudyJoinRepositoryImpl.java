package com.mos.backend.studyjoins.infrastructure;

import com.mos.backend.studyjoins.entity.StudyJoin;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class StudyJoinRepositoryImpl implements StudyJoinRepository {
    private final StudyJoinJpaRepository studyJoinJpaRepository;
    private final StudyJoinQueryDslRepository studyJoinQueryDslRepository;

    @Override
    public StudyJoin save(StudyJoin studyJoin) {
        return studyJoinJpaRepository.save(studyJoin);
    }

    @Override
    public Optional<StudyJoin> findById(Long studyApplicationId) {
        return studyJoinJpaRepository.findById(studyApplicationId);
    }

    @Override
    public List<StudyJoin> findAllByUserIdAndStatus(Long userId, String StudyJoinStatusCond) {
        return studyJoinQueryDslRepository.findAllByUserIdAndStatus(userId, StudyJoinStatusCond);
    }

    @Override
    public List<StudyJoin> findAllByStudyIdAndStatus(Long studyId, String StudyJoinStatusCond) {
        return studyJoinQueryDslRepository.findAllByStudyIdAndStatus(studyId, StudyJoinStatusCond);
    }

    @Override
    public boolean existsByUserIdAndStudyId(Long userId, Long studyId) {
        return studyJoinJpaRepository.existsByUserIdAndStudyId(userId, studyId);
    }
}

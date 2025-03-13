package com.mos.backend.studyparticipations.infrastructure;

import com.mos.backend.studyparticipations.entity.StudyApplication;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class StudyApplicationRepositoryImpl implements StudyApplicationRepository {
    private final StudyApplicationJpaRepository studyApplicationJpaRepository;

    @Override
    public Optional<StudyApplication> findById(Long studyApplicationId) {
        return studyApplicationJpaRepository.findById(studyApplicationId);
    }
}

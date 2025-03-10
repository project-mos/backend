package com.mos.backend.studies.infrastructure;

import com.mos.backend.studies.entity.Study;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class StudyRepositoryImpl implements StudyRepository{

    private final StudyJpaRepository studyJpaRepository;


    @Override
    public Study save(Study study) {
        return studyJpaRepository.save(study);
    }

    @Override
    public Optional<Study> findById(Long id) {
        return studyJpaRepository.findById(id);
    }

    @Override
    public void increaseViewCount(long studyId) {
        studyJpaRepository.increaseViewCount(studyId);
    }
}

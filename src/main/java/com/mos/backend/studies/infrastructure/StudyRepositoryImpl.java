package com.mos.backend.studies.infrastructure;

import com.mos.backend.studies.application.responsedto.StudiesResponseDto;
import com.mos.backend.studies.entity.Study;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class StudyRepositoryImpl implements StudyRepository{

    private final StudyJpaRepository studyJpaRepository;
    private final StudyQueryDslRepository studyQueryDSLRepository;


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

    @Override
    public Page<StudiesResponseDto> findStudies(Pageable pageable, String categoryCond, String meetingTypeCond, String recruitmentStatusCond, String progressStatusCond) {
        return studyQueryDSLRepository.findStudies(pageable, categoryCond, meetingTypeCond, recruitmentStatusCond, progressStatusCond);
    }

    @Override
    public long count() {
        return studyJpaRepository.count();
    }
}

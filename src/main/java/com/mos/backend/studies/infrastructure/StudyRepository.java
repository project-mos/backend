package com.mos.backend.studies.infrastructure;

import com.mos.backend.studies.entity.Study;

import java.util.Optional;

public interface StudyRepository {
    Study save(Study study);
    Optional<Study> findById(Long id);

    void increaseViewCount(long studyId);
}

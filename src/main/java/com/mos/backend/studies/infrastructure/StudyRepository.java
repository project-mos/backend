package com.mos.backend.studies.infrastructure;

import com.mos.backend.studies.application.responsedto.StudiesResponseDto;
import com.mos.backend.studies.entity.Study;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface StudyRepository {
    Study save(Study study);
    Optional<Study> findById(Long id);

    void increaseViewCount(long studyId);

    Page<StudiesResponseDto> findStudies(Pageable pageable, String categoryCond, String meetingTypeCond, String recruitmentStatusCond, String progressStatusCond);

    long count();
}

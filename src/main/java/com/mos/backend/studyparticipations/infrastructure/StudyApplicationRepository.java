package com.mos.backend.studyparticipations.infrastructure;

import com.mos.backend.studyparticipations.entity.StudyApplication;

import java.util.Optional;

public interface StudyApplicationRepository {
    Optional<StudyApplication> findById(Long studyApplicationId);
}

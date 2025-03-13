package com.mos.backend.studyparticipations.infrastructure;

import com.mos.backend.studyparticipations.entity.StudyApplication;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudyApplicationJpaRepository extends JpaRepository<StudyApplication, Long> {
}

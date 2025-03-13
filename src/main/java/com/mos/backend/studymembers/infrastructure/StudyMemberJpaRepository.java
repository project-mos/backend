package com.mos.backend.studymembers.infrastructure;

import com.mos.backend.studies.entity.Study;
import com.mos.backend.studymembers.entity.ParticipationStatus;
import com.mos.backend.studymembers.entity.StudyMember;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StudyMemberJpaRepository extends JpaRepository<StudyMember, Long> {
    long countByStudy(Study study);

    int countByStudyAndStatusIn(Study study, List<ParticipationStatus> statusList);
}
package com.mos.backend.studymembers.infrastructure;

import com.mos.backend.studies.entity.Study;
import com.mos.backend.studymembers.entity.ParticipationStatus;
import com.mos.backend.studymembers.entity.StudyMember;

import java.util.List;

public interface StudyMemberRepository {

    StudyMember save(StudyMember studyMember);
    long countByStudy(Study study);

    int countByStudyAndStatusIn(Study study, List<ParticipationStatus> statusList);
}

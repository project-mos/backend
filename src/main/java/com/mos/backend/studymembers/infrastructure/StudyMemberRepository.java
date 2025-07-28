package com.mos.backend.studymembers.infrastructure;

import com.mos.backend.studies.entity.Study;
import com.mos.backend.studymembers.entity.ParticipationStatus;
import com.mos.backend.studymembers.entity.StudyMember;
import com.mos.backend.studymembers.entity.StudyMemberRoleType;
import com.mos.backend.users.entity.User;

import java.util.List;
import java.util.Optional;

public interface StudyMemberRepository {

    StudyMember save(StudyMember studyMember);

    int countByStudyAndStatusIn(Study study, List<ParticipationStatus> statusList);

    Optional<StudyMember> findByStudyAndUser(Study study, User user);

    Optional<StudyMember> findById(Long studyMemberId);

    List<StudyMember> findAllByStudyId(Long studyId);

    Optional<StudyMember> findByUserIdAndStudyId(Long userId, Long studyId);

    boolean existsByStudyAndRoleType(Study study, StudyMemberRoleType roleType);

    boolean existsByUserAndStudy(User user, Study study);

    List<StudyMember> findAllByUserNotAndStudy(User user, Study study);
}

package com.mos.backend.studymembers.infrastructure;

import com.mos.backend.studies.entity.Study;
import com.mos.backend.studymembers.entity.ParticipationStatus;
import com.mos.backend.studymembers.entity.StudyMember;
import com.mos.backend.studymembers.entity.StudyMemberRoleType;
import com.mos.backend.users.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface StudyMemberJpaRepository extends JpaRepository<StudyMember, Long> {
    long countByStudy(Study study);

    int countByStudyAndStatusIn(Study study, List<ParticipationStatus> statusList);

    List<StudyMember> findAllByStudyId(Long studyId);

    Optional<StudyMember> findByUserIdAndStudyId(Long userId, Long studyId);

    boolean existsByStudyAndRoleType(Study study, StudyMemberRoleType roleType);

    Optional<StudyMember> findByStudyAndUser(Study study, User user);

    boolean existsByUserAndStudy(User user, Study study);

    List<StudyMember> findAllByUserNotAndStudy(User user, Study study);
}
package com.mos.backend.studymembers.infrastructure;

import com.mos.backend.studies.entity.Study;
import com.mos.backend.studymembers.entity.ParticipationStatus;
import com.mos.backend.studymembers.entity.StudyMember;
import com.mos.backend.studymembers.entity.StudyMemberRoleType;
import com.mos.backend.users.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class StudyMemberRepositoryImpl implements StudyMemberRepository{

    private final StudyMemberJpaRepository studyMemberJpaRepository;

    @Override
    public StudyMember save(StudyMember studyMember) {
        return studyMemberJpaRepository.save(studyMember);
    }

    @Override
    public int countByStudyAndStatusIn(Study study, List<ParticipationStatus> statusList) {
        return studyMemberJpaRepository.countByStudyAndStatusIn(study, statusList);
    }

    @Override
    public Optional<StudyMember> findByStudyAndUser(Study study, User user) {
        return studyMemberJpaRepository.findByStudyAndUser(study, user);
    }

    @Override
    public Optional<StudyMember> findById(Long studyMemberId) {
        return studyMemberJpaRepository.findById(studyMemberId);
    }

    @Override
    public List<StudyMember> findAllByStudyId(Long studyId) {
        return studyMemberJpaRepository.findAllByStudyId(studyId);
    }

    @Override
    public Optional<StudyMember> findByUserIdAndStudyId(Long userId, Long studyId) {
        return studyMemberJpaRepository.findByUserIdAndStudyId(userId, studyId);
    }

    @Override
    public boolean existsByStudyAndRoleType(Study study, StudyMemberRoleType roleType) {
        return studyMemberJpaRepository.existsByStudyAndRoleType(study, roleType);
    }

    @Override
    public boolean existsByUserAndStudy(User user, Study study) {
        return studyMemberJpaRepository.existsByUserAndStudy(user, study);
    }

    @Override
    public List<StudyMember> findAllByUserNotAndStudy(User user, Study study) {
        return studyMemberJpaRepository.findAllByUserNotAndStudy(user, study);
    }
}

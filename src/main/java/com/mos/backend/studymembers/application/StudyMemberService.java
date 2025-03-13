package com.mos.backend.studymembers.application;

import com.mos.backend.common.infrastructure.EntityFacade;
import com.mos.backend.studies.entity.Study;
import com.mos.backend.studymembers.entity.ParticipationStatus;
import com.mos.backend.studymembers.entity.StudyMember;
import com.mos.backend.studymembers.infrastructure.StudyMemberRepository;
import com.mos.backend.users.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StudyMemberService {
    private final StudyMemberRepository studyMemberRepository;
    private final EntityFacade entityFacade;

    @Transactional
    public void create(Long studyId, Long userId) {
        Study study = entityFacade.getStudy(studyId);
        User user = entityFacade.getUser(userId);

        studyMemberRepository.save(StudyMember.create(study, user));
    }


    public int countCurrentStudyMember(Long studyId) {
        Study study = entityFacade.getStudy(studyId);
        List<ParticipationStatus> currentParticipationStatusList = Arrays.asList(ParticipationStatus.ACTIVATED, ParticipationStatus.COMPLETED);
        return studyMemberRepository.countByStudyAndStatusIn(study, currentParticipationStatusList);
    }
}

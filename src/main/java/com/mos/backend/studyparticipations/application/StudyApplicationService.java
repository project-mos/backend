package com.mos.backend.studyparticipations.application;

import com.mos.backend.common.exception.MosException;
import com.mos.backend.common.infrastructure.EntityFacade;
import com.mos.backend.studies.entity.Study;
import com.mos.backend.studies.entity.exception.StudyErrorCode;
import com.mos.backend.studymembers.entity.StudyMember;
import com.mos.backend.studymembers.entity.exception.StudyMemberErrorCode;
import com.mos.backend.studymembers.infrastructure.StudyMemberRepository;
import com.mos.backend.studyparticipations.entity.StudyApplication;
import com.mos.backend.users.entity.User;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StudyApplicationService {

    private final StudyMemberRepository studyMemberRepository;
    private final EntityFacade entityFacade;

    @Transactional
    public void create(Long studyId, Long userId) {
        Study study = entityFacade.getStudy(studyId);
        User user = entityFacade.getUser(userId);

        studyMemberRepository.save(StudyMember.create(study, user));
    }

    @Transactional
    public void approveStudyApplication(Long userId, Long studyApplicationId) {
        User user = entityFacade.getUser(userId);
        StudyApplication studyApplication = entityFacade.getStudyApplication(studyApplicationId);

        Study study = studyApplication.getStudy();
        long studyMemberCnt = studyMemberRepository.countByStudy(study);

        validateStudyMemberCnt(studyMemberCnt, study);

        handleApprove(studyApplication, study, user);
    }

    @Transactional
    public void rejectStudyApplication(Long userId, Long studyApplicationId) {
        User user = entityFacade.getUser(userId);
        StudyApplication studyApplication = entityFacade.getStudyApplication(studyApplicationId);

        studyApplication.reject();
    }

    private static void validateStudyMemberCnt(long studyMemberCnt, Study study) {
        if (studyMemberCnt == 0)
            throw new MosException(StudyErrorCode.STUDY_NOT_FOUND);

        if (studyMemberCnt >= study.getMaxStudyMemberCount())
            throw new MosException(StudyMemberErrorCode.STUDY_MEMBER_FULL);
    }

    private void handleApprove(StudyApplication studyApplication, Study study, User user) {
        studyApplication.approve();
        studyMemberRepository.save(StudyMember.create(study, user));
    }
}

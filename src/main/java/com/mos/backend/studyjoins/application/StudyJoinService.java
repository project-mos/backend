package com.mos.backend.studyjoins.application;

import com.mos.backend.common.event.Event;
import com.mos.backend.common.event.EventType;
import com.mos.backend.common.exception.MosException;
import com.mos.backend.common.infrastructure.EntityFacade;
import com.mos.backend.questionanswers.infrastructure.QuestionAnswerRepository;
import com.mos.backend.studies.application.StudyService;
import com.mos.backend.studies.entity.Study;
import com.mos.backend.studyjoins.application.event.StudyJoinCreatedEventPayload;
import com.mos.backend.studyjoins.application.res.MyStudyJoinRes;
import com.mos.backend.studyjoins.application.res.QuestionAnswerRes;
import com.mos.backend.studyjoins.application.res.StudyJoinRes;
import com.mos.backend.studyjoins.entity.StudyJoin;
import com.mos.backend.studyjoins.entity.exception.StudyJoinErrorCode;
import com.mos.backend.studyjoins.infrastructure.StudyJoinRepository;
import com.mos.backend.studyjoins.presentation.controller.req.StudyJoinReq;
import com.mos.backend.studymembers.application.StudyMemberService;
import com.mos.backend.studyquestions.application.StudyQuestionService;
import com.mos.backend.studyquestions.entity.QuestionType;
import com.mos.backend.studyquestions.entity.StudyQuestion;
import com.mos.backend.studyquestions.infrastructure.StudyQuestionRepository;
import com.mos.backend.users.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StudyJoinService {

    private final StudyMemberService studyMemberService;
    private final StudyJoinRepository studyJoinRepository;
    private final StudyQuestionRepository studyQuestionRepository;
    private final QuestionAnswerRepository questionAnswerRepository;
    private final EntityFacade entityFacade;
    private final ApplicationEventPublisher eventPublisher;
    private final StudyQuestionService studyQuestionService;
    private final StudyService studyService;

    @Transactional
    public void joinStudy(Long userId, Long studyId, List<StudyJoinReq> studyJoinReqs) {
        User user = entityFacade.getUser(userId);
        Study study = entityFacade.getStudy(studyId);

        validateJoinableStudy(user, study);

        StudyJoin newStudyJoin = saveStudyJoin(user, study);

        List<StudyQuestion> requiredQuestions = studyQuestionRepository.findByStudyIdAndRequiredTrue(study.getId());
        validateRequiredQuestions(studyJoinReqs, requiredQuestions);

        if (studyJoinReqs != null && studyJoinReqs.size() > 0) {
            saveQuestionAnswers(studyJoinReqs, study, newStudyJoin);
        }
//        eventPublisher.publishEvent(new Event<>(EventType.STUDY_JOINED, new StudyJoinEventPayloadWithNotification(userId, HotStudyEventType.JOIN, studyId)));
    }

    private void saveQuestionAnswers(List<StudyJoinReq> studyJoinReqs, Study study, StudyJoin newStudyJoin) {
        for (StudyJoinReq studyJoinReq : studyJoinReqs) {
            StudyQuestion studyQuestion = entityFacade.getStudyQuestion(studyJoinReq.getStudyQuestionId());

            validateSameStudy(studyQuestion, study);
            validateQuestion(studyJoinReq, studyQuestion);

            eventPublisher.publishEvent(
                    new Event<>(
                            EventType.STUDY_JOINED,
                            new StudyJoinCreatedEventPayload(newStudyJoin.getId(), studyQuestion.getId(), studyJoinReq.getAnswer())
                    )
            );
        }
    }

    private void validateJoinableStudy(User user, Study study) {
        studyService.validateRecruitmentPeriod(study);
        studyService.validateRecruitmentStatus(study);

        validateStudyJoinConflict(user, study);
    }

    private void validateStudyJoinConflict(User user, Study study) {
        if (studyJoinRepository.existsByUserIdAndStudyId(user.getId(), study.getId()))
            throw new MosException(StudyJoinErrorCode.CONFLICT);
    }

    private static void validateQuestion(StudyJoinReq studyJoinReq, StudyQuestion studyQuestion) {
        // 필수 질문 답변 검증
        if (studyQuestion.isRequired()) {
            if (studyJoinReq.getAnswer() == null || studyJoinReq.getAnswer().isBlank())
                throw new MosException(StudyJoinErrorCode.MISSING_REQUIRED_QUESTIONS);
        }

        // 답변 옵션 검증 (객관식만)
        if (studyQuestion.getType().equals(QuestionType.MULTIPLE_CHOICE) && studyJoinReq.getAnswer() != null && !studyJoinReq.getAnswer().isBlank()) {
            List<String> questionOptionList = studyQuestion.getOptions().toList();
            if (!questionOptionList.contains(studyJoinReq.getAnswer())) {
                throw new MosException(StudyJoinErrorCode.INVALID_ANSWER_OPTION);
            }
        }
    }

    @Transactional(readOnly = true)
    @PreAuthorize("@studySecurity.isLeaderOrAdmin(#studyId)")
    public List<StudyJoinRes> getStudyJoins(Long studyId, String studyJoinStatusCond) {
        Study study = entityFacade.getStudy(studyId);

        List<StudyJoin> studyJoins = studyJoinRepository.findAllByStudyIdAndStatus(study.getId(), studyJoinStatusCond);

        return studyJoins.stream()
                .map(studyJoin -> {
                    List<QuestionAnswerRes> questionAnswers = questionAnswerRepository.findAllByStudyJoinId(studyJoin.getId());
                    return StudyJoinRes.of(studyJoin, questionAnswers);
                })
                .toList();
    }

    @Transactional
    @PreAuthorize("@studySecurity.isLeaderOrAdmin(#studyId)")
    public void approveStudyJoin(Long studyId, Long studyJoinId) {
        Study study = entityFacade.getStudy(studyId);
        StudyJoin studyJoin = entityFacade.getStudyJoin(studyJoinId);

        validateSameStudy(studyJoin, study);

        handleApprove(studyJoin, study);
    }

    @Transactional
    @PreAuthorize("@studySecurity.isLeaderOrAdmin(#studyId)")
    public void rejectStudyJoin(Long studyId, Long studyJoinId) {
        Study study = entityFacade.getStudy(studyId);
        StudyJoin studyJoin = entityFacade.getStudyJoin(studyJoinId);

        validateSameStudy(studyJoin, study);

        studyJoin.reject();
    }

    @Transactional
    @PreAuthorize("@studySecurity.isApplicantOrAdmin(#studyJoinId)")
    public void cancelStudyJoin(Long studyId, Long studyJoinId) {
        Study study = entityFacade.getStudy(studyId);
        StudyJoin studyJoin = entityFacade.getStudyJoin(studyJoinId);

        validateSameStudy(studyJoin, study);
        validatePendingStatus(studyJoin);

        studyJoin.cancel();
//        eventPublisher.publishEvent(new Event<>(EventType.STUDY_JOIN_CANCELED, new StudyJoinEventPayloadWithNotification(userId, HotStudyEventType.JOIN_CANCEL, studyId)));
    }

    @Transactional(readOnly = true)
    @PreAuthorize("@userSecurity.isOwnerOrAdmin(#userId)")
    public List<MyStudyJoinRes> getMyStudyJoins(Long userId, String studyJoinStatusCond) {
        User user = entityFacade.getUser(userId);

        List<StudyJoin> studyJoins = studyJoinRepository.findAllByUserIdAndStatus(userId, studyJoinStatusCond);

        return studyJoins.stream().map(MyStudyJoinRes::from).toList();
    }

    private void validateSameStudy(StudyQuestion studyQuestion, Study study) {
        studyQuestionService.validateSameStudy(studyQuestion, study);
    }

    private StudyJoin saveStudyJoin(User user, Study study) {
        StudyJoin newStudyJoin = StudyJoin.createPendingStudyJoin(user, study);
        return studyJoinRepository.save(newStudyJoin);
    }

    private static void validateRequiredQuestions(List<StudyJoinReq> studyJoinReqs, List<StudyQuestion> requiredQuestions) {
        Set<Long> requiredQuestionIds = requiredQuestions.stream().map(StudyQuestion::getId).collect(Collectors.toSet());
        Set<Long> requestQuestionIds = studyJoinReqs == null ? Collections.EMPTY_SET : studyJoinReqs.stream().map(StudyJoinReq::getStudyQuestionId).collect(Collectors.toSet());
        if (!requestQuestionIds.containsAll(requiredQuestionIds))
            throw new MosException(StudyJoinErrorCode.MISSING_REQUIRED_QUESTIONS);
    }

    private static void validatePendingStatus(StudyJoin studyJoin) {
        if (!studyJoin.isPending())
            throw new MosException(StudyJoinErrorCode.STUDY_JOIN_NOT_PENDING);
    }

    private static void validateSameStudy(StudyJoin studyJoin, Study study) {
        if (!studyJoin.isSameStudy(study))
            throw new MosException(StudyJoinErrorCode.STUDY_JOIN_MISMATCH);
    }

    private void handleApprove(StudyJoin studyJoin, Study study) {
        studyMemberService.createStudyMember(study.getId(), studyJoin.getUser().getId());
        studyJoin.approve();
    }
}

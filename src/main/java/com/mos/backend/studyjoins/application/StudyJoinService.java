package com.mos.backend.studyjoins.application;

import com.mos.backend.common.event.Event;
import com.mos.backend.common.event.EventType;
import com.mos.backend.common.exception.MosException;
import com.mos.backend.common.infrastructure.EntityFacade;
import com.mos.backend.hotstudies.entity.HotStudyEventType;
import com.mos.backend.questionanswers.entity.QuestionAnswer;
import com.mos.backend.questionanswers.infrastructure.QuestionAnswerRepository;
import com.mos.backend.studies.entity.Study;
import com.mos.backend.studyjoins.application.event.StudyJoinEventPayloadWithNotification;
import com.mos.backend.studyjoins.application.res.MyStudyJoinRes;
import com.mos.backend.studyjoins.application.res.QuestionAnswerRes;
import com.mos.backend.studyjoins.application.res.StudyJoinRes;
import com.mos.backend.studyjoins.entity.StudyJoin;
import com.mos.backend.studyjoins.entity.StudyJoinStatus;
import com.mos.backend.studyjoins.entity.exception.StudyJoinErrorCode;
import com.mos.backend.studyjoins.infrastructure.StudyJoinRepository;
import com.mos.backend.studyjoins.presentation.controller.req.StudyJoinReq;
import com.mos.backend.studymembers.application.StudyMemberService;
import com.mos.backend.studyquestions.entity.StudyQuestion;
import com.mos.backend.studyquestions.entity.StudyQuestionErrorCode;
import com.mos.backend.studyquestions.infrastructure.StudyQuestionRepository;
import com.mos.backend.users.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Transactional
    public void joinStudy(Long userId, Long studyId, List<StudyJoinReq> studyJoinReqs) {
        User user = entityFacade.getUser(userId);
        Study study = entityFacade.getStudy(studyId);

        List<StudyQuestion> requiredQuestions = studyQuestionRepository.findByStudyIdAndRequiredTrue(study.getId());
        validateRequiredQuestions(studyJoinReqs, requiredQuestions);

        StudyJoin newStudyJoin = saveStudyJoin(user, study);

        for (StudyJoinReq studyJoinReq : studyJoinReqs) {
            StudyQuestion studyQuestion = entityFacade.getStudyQuestion(studyJoinReq.getStudyQuestionId());

            validateSameStudy(studyQuestion, study);

            saveQuestionAnswers(newStudyJoin, studyQuestion, studyJoinReq.getAnswer());
        }
        eventPublisher.publishEvent(new Event<>(EventType.STUDY_JOINED, new StudyJoinEventPayloadWithNotification(userId, HotStudyEventType.JOIN, studyId)));
    }

    @Transactional(readOnly = true)
    public List<StudyJoinRes> getStudyJoins(Long userId, Long studyId) {
        User user = entityFacade.getUser(userId);
        Study study = entityFacade.getStudy(studyId);

        List<StudyJoin> studyJoins = studyJoinRepository.findAllByStudyId(study.getId());

        return studyJoins.stream()
                .map(studyJoin -> {
                    List<QuestionAnswerRes> questionAnswers = questionAnswerRepository.findAllByStudyJoinId(studyJoin.getId());
                    return StudyJoinRes.of(studyJoin, questionAnswers);
                })
                .toList();
    }

    @Transactional
    public void approveStudyJoin(Long userId, Long studyId, Long studyJoinId) {
        User user = entityFacade.getUser(userId);
        Study study = entityFacade.getStudy(studyId);
        StudyJoin studyJoin = entityFacade.getStudyJoin(studyJoinId);

        validateSameStudy(studyJoin, study);

        handleApprove(studyJoin, study);
    }

    @Transactional
    public void rejectStudyJoin(Long userId, Long studyId, Long studyJoinId) {
        User user = entityFacade.getUser(userId);
        Study study = entityFacade.getStudy(studyId);
        StudyJoin studyJoin = entityFacade.getStudyJoin(studyJoinId);

        validateSameStudy(studyJoin, study);

        studyJoin.reject();
    }

    @Transactional
    public void cancelStudyJoin(Long userId, Long studyId, Long studyJoinId) {
        User user = entityFacade.getUser(userId);
        Study study = entityFacade.getStudy(studyId);
        StudyJoin studyJoin = entityFacade.getStudyJoin(studyJoinId);

        validateSameStudy(studyJoin, study);
        validatePendingStatus(studyJoin);

        studyJoin.cancel();
        eventPublisher.publishEvent(new Event<>(EventType.STUDY_JOIN_CANCELED, new StudyJoinEventPayloadWithNotification(userId, HotStudyEventType.JOIN_CANCEL, studyId)));
    }

    @Transactional(readOnly = true)
    public List<MyStudyJoinRes> getMyStudyJoins(Long userId, String status) {
        User user = entityFacade.getUser(userId);

        StudyJoinStatus studyJoinStatus = StudyJoinStatus.fromDescription(status);

        List<StudyJoin> studyJoins = studyJoinRepository.findAllByStatusWithStudy(studyJoinStatus);

        return studyJoins.stream().map(MyStudyJoinRes::from).toList();
    }

    private void saveQuestionAnswers(StudyJoin newStudyJoin, StudyQuestion studyQuestion, String answer) {
        QuestionAnswer questionAnswer = new QuestionAnswer(newStudyJoin, studyQuestion, answer);
        questionAnswerRepository.save(questionAnswer);
    }

    private static void validateSameStudy(StudyQuestion studyQuestion, Study study) {
        if (!studyQuestion.isSameStudy(study))
            throw new MosException(StudyQuestionErrorCode.STUDY_QUESTION_MISMATCH);
    }

    private StudyJoin saveStudyJoin(User user, Study study) {
        StudyJoin newStudyJoin = StudyJoin.createPendingStudyJoin(user, study);
        return studyJoinRepository.save(newStudyJoin);
    }

    private static void validateRequiredQuestions(List<StudyJoinReq> studyJoinReqs, List<StudyQuestion> requiredQuestions) {
        Set<Long> requiredQuestionIds = requiredQuestions.stream().map(StudyQuestion::getId).collect(Collectors.toSet());
        Set<Long> requestQuestionIds = studyJoinReqs.stream().map(StudyJoinReq::getStudyQuestionId).collect(Collectors.toSet());
        if (!requiredQuestionIds.equals(requestQuestionIds))
            throw new MosException(StudyQuestionErrorCode.MISSING_REQUIRED_QUESTIONS);
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

package com.mos.backend.studyjoins.application;

import com.mos.backend.common.exception.MosException;
import com.mos.backend.common.infrastructure.EntityFacade;
import com.mos.backend.questionanswers.entity.QuestionAnswer;
import com.mos.backend.questionanswers.infrastructure.QuestionAnswerRepository;
import com.mos.backend.studies.application.StudyService;
import com.mos.backend.studies.entity.Category;
import com.mos.backend.studies.entity.Study;
import com.mos.backend.studies.entity.exception.StudyErrorCode;
import com.mos.backend.studyjoins.application.res.MyStudyJoinRes;
import com.mos.backend.studyjoins.application.res.QuestionAnswerRes;
import com.mos.backend.studyjoins.application.res.StudyJoinRes;
import com.mos.backend.studyjoins.entity.StudyJoin;
import com.mos.backend.studyjoins.entity.StudyJoinStatus;
import com.mos.backend.studyjoins.entity.exception.StudyJoinErrorCode;
import com.mos.backend.studyjoins.infrastructure.StudyJoinRepository;
import com.mos.backend.studyjoins.presentation.controller.req.StudyJoinReq;
import com.mos.backend.studymembers.application.StudyMemberService;
import com.mos.backend.studymembers.entity.StudyMember;
import com.mos.backend.studymembers.infrastructure.StudyMemberRepository;
import com.mos.backend.studyquestions.application.StudyQuestionService;
import com.mos.backend.studyquestions.entity.QuestionType;
import com.mos.backend.studyquestions.entity.StudyQuestion;
import com.mos.backend.studyquestions.entity.StudyQuestionErrorCode;
import com.mos.backend.studyquestions.infrastructure.StudyQuestionRepository;
import com.mos.backend.users.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.context.ApplicationEventPublisher;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@MockitoSettings(strictness = Strictness.LENIENT)
@ExtendWith(MockitoExtension.class)
@DisplayName("StudyJoinService 테스트")
public class StudyJoinServiceTest {

    @Mock
    private StudyMemberService studyMemberService;
    @Mock
    private StudyMemberRepository studyMemberRepository;
    @Mock
    private StudyJoinRepository studyJoinRepository;
    @Mock
    private QuestionAnswerRepository questionAnswerRepository;
    @Mock
    private StudyQuestionRepository studyQuestionRepository;
    @Mock
    private EntityFacade entityFacade;
    @Mock
    private ApplicationEventPublisher eventPublisher;
    @Mock
    private StudyQuestionService studyQuestionService;
    @Mock
    private StudyService studyService;

    @InjectMocks
    private StudyJoinService studyJoinService;

    @Nested
    @DisplayName("스터디 신청 성공 시나리오")
    class JoinStudySuccessScenarios {
        @Test
        @DisplayName("스터디 참여 신청 성공")
        void joinStudy_Success() {
            // Given
            Long userId = 1L;
            Long studyId = 1L;
            User user = mock(User.class);
            Study study = mock(Study.class);
            StudyJoin newStudyJoin = mock(StudyJoin.class);
            StudyQuestion studyQuestion = mock(StudyQuestion.class);
            StudyJoinReq studyJoinReq = new StudyJoinReq(1L, "answer");
            List<StudyJoinReq> studyJoinReqs = List.of(studyJoinReq);
            StudyQuestion requiredStudyQuestion = mock(StudyQuestion.class);
            List<StudyQuestion> requiredQuestions = List.of(requiredStudyQuestion);

            when(entityFacade.getUser(userId)).thenReturn(user);
            when(entityFacade.getStudy(studyId)).thenReturn(study);
            when(study.getId()).thenReturn(studyId);
            when(studyQuestionRepository.findByStudyIdAndRequiredTrue(studyId)).thenReturn(requiredQuestions);
            when(requiredStudyQuestion.getId()).thenReturn(1L);
            when(studyJoinRepository.save(any(StudyJoin.class))).thenReturn(newStudyJoin);
            when(entityFacade.getStudyQuestion(1L)).thenReturn(studyQuestion);
            when(studyQuestion.getType()).thenReturn(QuestionType.SHORT_ANSWER);

            // When
            studyJoinService.joinStudy(userId, studyId, studyJoinReqs);

            // Then
            verify(entityFacade).getUser(userId);
            verify(entityFacade).getStudy(studyId);
            verify(studyQuestionRepository).findByStudyIdAndRequiredTrue(studyId);
            verify(studyJoinRepository).save(any(StudyJoin.class));
            verify(entityFacade).getStudyQuestion(1L);
        }
    }

    @Nested
    @DisplayName("스터디 신청 실패 시나리오")
    class JoinStudyFailureScenarios {
        @Test
        @DisplayName("필수 질문이 누락된 경우 MosException 발생")
        void joinStudy_Failure_MissingRequiredQuestions() {
            // Given
            Long userId = 1L;
            Long studyId = 1L;
            Long requiredStudyQuestionId = 1L;
            User user = mock(User.class);
            Study study = mock(Study.class);
            StudyJoin newStudyJoin = mock(StudyJoin.class);
            StudyQuestion studyQuestion = mock(StudyQuestion.class);

            StudyJoinReq studyJoinReq = new StudyJoinReq(2L, "answer"); // 필수 질문의 id=1, 하지만 요청은 id=2
            List<StudyJoinReq> studyJoinReqs = List.of(studyJoinReq);

            StudyQuestion requiredStudyQuestion = mock(StudyQuestion.class);
            when(requiredStudyQuestion.getId()).thenReturn(requiredStudyQuestionId);
            List<StudyQuestion> requiredQuestions = List.of(requiredStudyQuestion);

            when(entityFacade.getUser(userId)).thenReturn(user);
            when(entityFacade.getStudy(studyId)).thenReturn(study);
            when(study.getId()).thenReturn(studyId);
            when(studyQuestionRepository.findByStudyIdAndRequiredTrue(studyId)).thenReturn(requiredQuestions);
            when(studyJoinRepository.save(any(StudyJoin.class))).thenReturn(newStudyJoin);
            when(entityFacade.getStudyQuestion(2L)).thenReturn(studyQuestion);

            // When
            MosException exception = assertThrows(MosException.class, () -> {
                studyJoinService.joinStudy(userId, studyId, studyJoinReqs);
            });

            // Then
            assertEquals(StudyJoinErrorCode.MISSING_REQUIRED_QUESTIONS, exception.getErrorCode());
            verify(entityFacade).getUser(userId);
            verify(entityFacade).getStudy(studyId);
            verify(studyQuestionRepository).findByStudyIdAndRequiredTrue(studyId);
            verify(entityFacade, never()).getStudyQuestion(any(Long.class));
            verify(studyQuestion, never()).isSameStudy(study);
            verify(questionAnswerRepository, never()).save(any(QuestionAnswer.class));
        }

        @Test
        @DisplayName("스터디 질문이 스터디와 일치하지 않는 경우 MosException 발생")
        void joinStudy_Failure_StudyQuestionMismatch() {
            Long userId = 1L;
            Long studyId = 1L;
            User user = mock(User.class);
            Study study = mock(Study.class);
            StudyJoin newStudyJoin = mock(StudyJoin.class);
            StudyQuestion studyQuestion = mock(StudyQuestion.class);
            StudyJoinReq studyJoinReq = new StudyJoinReq(1L, "answer");
            List<StudyJoinReq> studyJoinReqs = List.of(studyJoinReq);

            StudyQuestion requiredStudyQuestion = mock(StudyQuestion.class);
            List<StudyQuestion> requiredQuestions = List.of(requiredStudyQuestion);

            when(entityFacade.getUser(userId)).thenReturn(user);
            when(entityFacade.getStudy(studyId)).thenReturn(study);
            when(study.getId()).thenReturn(studyId);
            when(studyQuestionRepository.findByStudyIdAndRequiredTrue(studyId)).thenReturn(requiredQuestions);
            when(requiredStudyQuestion.getId()).thenReturn(1L);
            when(studyJoinRepository.save(any(StudyJoin.class))).thenReturn(newStudyJoin);
            when(entityFacade.getStudyQuestion(1L)).thenReturn(studyQuestion);
            doThrow(new MosException(StudyQuestionErrorCode.STUDY_QUESTION_MISMATCH)).when(studyQuestionService).validateSameStudy(studyQuestion, study);

            MosException exception = assertThrows(MosException.class, () -> {
                studyJoinService.joinStudy(userId, studyId, studyJoinReqs);
            });

            assertEquals(StudyQuestionErrorCode.STUDY_QUESTION_MISMATCH, exception.getErrorCode());
            verify(entityFacade).getUser(userId);
            verify(entityFacade).getStudy(studyId);
            verify(studyQuestionRepository).findByStudyIdAndRequiredTrue(studyId);
            verify(entityFacade).getStudyQuestion(1L);
            verify(studyJoinRepository).save(any(StudyJoin.class));
            verify(questionAnswerRepository, never()).save(any(QuestionAnswer.class));
        }
    }

    @Nested
    @DisplayName("스터디의 스터디 신청 목록 조회 성공 시나리오")
    class GetStudyJoinSuccessScenarios {
        @Test
        @DisplayName("스터디의 스터디 신청 목록 조회")
        void getStudyJoins_Success() {
            // Given
            Long studyId = 1L;
            Long studyJoinId1 = 1L;
            Long studyJoinId2 = 2L;
            User user = mock(User.class);
            Study mockStudy = mock(Study.class);
            StudyJoin mockStudyJoin1 = mock(StudyJoin.class);
            StudyJoin mockStudyJoin2 = mock(StudyJoin.class);
            List<StudyJoin> mockStudyJoins = List.of(mockStudyJoin1, mockStudyJoin2);
            QuestionAnswerRes questionAnswerRes1 = mock(QuestionAnswerRes.class);
            QuestionAnswerRes questionAnswerRes2 = mock(QuestionAnswerRes.class);
            List<QuestionAnswerRes> mockQuestionAnswers = List.of(questionAnswerRes1, questionAnswerRes2);
            StudyJoinStatus status = StudyJoinStatus.PENDING;

            when(entityFacade.getStudy(studyId)).thenReturn(mockStudy);
            when(studyJoinRepository.findAllByStudyIdAndStatus(studyId, null)).thenReturn(mockStudyJoins);
            when(mockStudy.getId()).thenReturn(studyId);
            when(mockStudyJoin1.getStatus()).thenReturn(status);
            when(mockStudyJoin2.getStatus()).thenReturn(status);
            when(questionAnswerRepository.findAllByStudyJoinId(studyJoinId1)).thenReturn(mockQuestionAnswers);
            when(questionAnswerRepository.findAllByStudyJoinId(studyJoinId2)).thenReturn(mockQuestionAnswers);
            when(mockStudyJoin1.getId()).thenReturn(studyJoinId1);
            when(mockStudyJoin2.getId()).thenReturn(studyJoinId2);
            when(mockStudyJoin1.getUser()).thenReturn(user);
            when(mockStudyJoin2.getUser()).thenReturn(user);

            // When
            List<StudyJoinRes> studyJoinResList = studyJoinService.getStudyJoins(studyId, null);

            // Then
            verify(entityFacade).getStudy(studyId);
            verify(questionAnswerRepository).findAllByStudyJoinId(1L);
            verify(questionAnswerRepository).findAllByStudyJoinId(2L);
            assertNotNull(studyJoinResList);
            assertEquals(2, studyJoinResList.size());
        }
    }

    @Nested
    @DisplayName("나의 스터디 신청 조회 성공 시나리오")
    class GetMyStudyJoinSuccessScenarios {
        @Test
        @DisplayName("나의 스터디 신청 조회")
        void getMyStudyJoin_Success() {
            // Given
            Long userId = 1L;
            User user = mock(User.class);
            Study mockStudy1 = mock(Study.class);
            StudyJoin mockStudyJoin1 = mock(StudyJoin.class);
            Study mockStudy2 = mock(Study.class);
            StudyJoin mockStudyJoin2 = mock(StudyJoin.class);
            List<StudyJoin> mockStudyJoins = List.of(mockStudyJoin1, mockStudyJoin2);
            StudyJoinStatus status = StudyJoinStatus.PENDING;

            when(entityFacade.getUser(userId)).thenReturn(user);
            when(mockStudyJoin1.getStudy()).thenReturn(mockStudy1);
            when(mockStudyJoin2.getStudy()).thenReturn(mockStudy2);
            when(studyJoinRepository.findAllByUserIdAndStatus(userId, status.getDescription())).thenReturn(mockStudyJoins);
            when(mockStudy1.getCategory()).thenReturn(mock(Category.class));
            when(mockStudy2.getCategory()).thenReturn(mock(Category.class));
            when(mockStudyJoin1.getStatus()).thenReturn(status);
            when(mockStudyJoin2.getStatus()).thenReturn(status);

            // When
            List<MyStudyJoinRes> myStudyJoinResList = studyJoinService.getMyStudyJoins(userId, status.getDescription());

            // Then
            verify(entityFacade).getUser(userId);
            verify(studyJoinRepository).findAllByUserIdAndStatus(userId, status.getDescription());
            assertThat(myStudyJoinResList).hasSize(mockStudyJoins.size());
        }
    }

    @Nested
    @DisplayName("스터디 신청 승인 성공 시나리오")
    class ApproveSuccessScenarios {
        @Test
        @DisplayName("스터디 신청 승인")
        void approveStudyMember_Success() {
            // Given
            Long userId = 1L;
            Long studyId = 1L;
            Long studyJoinId = 1L;
            User mockUser = mock(User.class);
            Study mockStudy = mock(Study.class);
            StudyJoin mockStudyJoin = mock(StudyJoin.class);

            when(entityFacade.getStudy(studyId)).thenReturn(mockStudy);
            when(entityFacade.getStudyJoin(studyJoinId)).thenReturn(mockStudyJoin);
            when(mockStudyJoin.getUser()).thenReturn(mockUser);
            when(mockUser.getId()).thenReturn(userId);
            when(mockStudyJoin.isSameStudy(mockStudy)).thenReturn(true);
            when(mockStudy.getMaxStudyMemberCount()).thenReturn(5);

            // When
            studyJoinService.approveStudyJoin(studyId, studyJoinId);

            // Then
            verify(entityFacade).getStudy(studyId);
            verify(entityFacade).getStudyJoin(studyJoinId);
            verify(mockStudyJoin).isSameStudy(mockStudy);
            verify(mockStudyJoin).approve();
        }
    }

    @Nested
    @DisplayName("스터디 신청 승인 실패 시나리오")
    class ApproveFailureScenarios {
        @Test
        @DisplayName("스터디가 존재하지 않을 때 MosException 발생")
        void approveStudyMember_StudyNotFound() {
            // Given
            Long userId = 1L;
            Long studyId = 1L;
            Long studyJoinId = 1L;
            User mockUser = mock(User.class);
            Study mockStudy = mock(Study.class);
            StudyJoin mockStudyJoin = mock(StudyJoin.class);

            when(entityFacade.getUser(userId)).thenReturn(mockUser);
            when(entityFacade.getStudy(studyId)).thenThrow(new MosException(StudyErrorCode.STUDY_NOT_FOUND));

            // When & Then
            MosException exception = assertThrows(MosException.class, () -> {
                studyJoinService.approveStudyJoin(studyId, studyJoinId);
            });

            assertEquals(StudyErrorCode.STUDY_NOT_FOUND, exception.getErrorCode());

            verify(entityFacade).getStudy(studyId);
            verify(entityFacade, never()).getStudyJoin(studyJoinId);
            verify(mockStudyJoin, never()).isSameStudy(mockStudy);
            verify(mockStudyJoin, never()).approve();
            verify(studyMemberRepository, never()).save(any(StudyMember.class));
        }

        @Test
        @DisplayName("스터디가입이 요청한 스터디와 일치하지 않을 때 MosException 발생")
        void approveStudyMember_StudyJoinMismatch() {
            // Given
            Long userId = 1L;
            Long studyId = 1L;
            Long studyJoinId = 1L;
            User mockUser = mock(User.class);
            Study mockStudy = mock(Study.class);
            StudyJoin mockStudyJoin = mock(StudyJoin.class);

            when(entityFacade.getUser(userId)).thenReturn(mockUser);
            when(entityFacade.getStudy(studyId)).thenReturn(mockStudy);
            when(entityFacade.getStudyJoin(studyJoinId)).thenReturn(mockStudyJoin);
            when(mockStudyJoin.isSameStudy(mockStudy)).thenReturn(false);

            // When & Then
            MosException exception = assertThrows(MosException.class, () -> {
                studyJoinService.approveStudyJoin(studyId, studyJoinId);
            });

            assertEquals(StudyJoinErrorCode.STUDY_JOIN_MISMATCH, exception.getErrorCode());

            verify(entityFacade).getStudy(studyId);
            verify(entityFacade).getStudyJoin(studyJoinId);
            verify(mockStudyJoin).isSameStudy(mockStudy);
            verify(mockStudyJoin, never()).approve();
            verify(studyMemberRepository, never()).save(any(StudyMember.class));
        }

    }

    @Nested
    @DisplayName("스터디 신청 거절 성공 시나리오")
    class RejectSuccessScenarios {
        @Test
        @DisplayName("스터디 신청 거절")
        void rejectStudyMember_Success() {
            // Given
            Long studyId = 1L;
            Long studyJoinId = 1L;
            Study mockStudy = mock(Study.class);
            StudyJoin mockStudyJoin = mock(StudyJoin.class);

            when(entityFacade.getStudy(studyId)).thenReturn(mockStudy);
            when(entityFacade.getStudyJoin(studyJoinId)).thenReturn(mockStudyJoin);
            when(mockStudyJoin.isSameStudy(mockStudy)).thenReturn(true);

            // When
            studyJoinService.rejectStudyJoin(studyId, studyJoinId);

            // Then
            verify(entityFacade).getStudy(studyId);
            verify(entityFacade).getStudyJoin(studyJoinId);
            verify(mockStudyJoin).isSameStudy(mockStudy);
            verify(mockStudyJoin).reject();
        }
    }

    @Nested
    @DisplayName("스터디 신청 거절 실패 시나리오")
    class RejectFailureScenarios {
        @Test
        @DisplayName("스터디가입이 요청한 스터디와 일치하지 않을 때 MosException 발생")
        void rejectStudyMember_StudyJoinMismatch() {
            // Given
            Long studyId = 1L;
            Long studyJoinId = 1L;
            Study mockStudy = mock(Study.class);
            StudyJoin mockStudyJoin = mock(StudyJoin.class);

            when(entityFacade.getStudy(studyId)).thenReturn(mockStudy);
            when(entityFacade.getStudyJoin(studyJoinId)).thenReturn(mockStudyJoin);
            when(mockStudyJoin.isSameStudy(mockStudy)).thenReturn(false);

            // When & Then
            MosException exception = assertThrows(MosException.class, () -> {
                studyJoinService.rejectStudyJoin(studyId, studyJoinId);
            });

            assertEquals(StudyJoinErrorCode.STUDY_JOIN_MISMATCH, exception.getErrorCode());

            verify(entityFacade).getStudy(studyId);
            verify(entityFacade).getStudyJoin(studyJoinId);
            verify(mockStudyJoin).isSameStudy(mockStudy);
            verify(mockStudyJoin, never()).reject();
        }
    }

    @Nested
    @DisplayName("스터디 신청 취소 성공 시나리오")
    class CancelSuccessScenarios {
        @Test
        @DisplayName("스터디 신청 취소")
        void cancelStudyMember_Success() {
            // Given
            Long studyId = 1L;
            Long studyJoinId = 1L;
            Study mockStudy = mock(Study.class);
            StudyJoin mockStudyJoin = mock(StudyJoin.class);

            when(entityFacade.getStudy(studyId)).thenReturn(mockStudy);
            when(entityFacade.getStudyJoin(studyJoinId)).thenReturn(mockStudyJoin);
            when(mockStudyJoin.isPending()).thenReturn(true);
            when(mockStudyJoin.isSameStudy(mockStudy)).thenReturn(true);

            // When
            studyJoinService.cancelStudyJoin(studyId, studyJoinId);

            // Then
            verify(entityFacade).getStudy(studyId);
            verify(entityFacade).getStudyJoin(studyJoinId);
            verify(mockStudyJoin).isSameStudy(mockStudy);
            verify(mockStudyJoin).cancel();
        }
    }

    @Nested
    @DisplayName("스터디 신청 취소 실패 시나리오")
    class CancelFailureScenarios {
        @Test
        @DisplayName("스터디가입이 요청한 스터디와 일치하지 않을 때 MosException 발생")
        void cancelStudyMember_StudyJoinMismatch() {
            // Given
            Long studyId = 1L;
            Long studyJoinId = 1L;
            Study mockStudy = mock(Study.class);
            StudyJoin mockStudyJoin = mock(StudyJoin.class);

            when(entityFacade.getStudy(studyId)).thenReturn(mockStudy);
            when(entityFacade.getStudyJoin(studyJoinId)).thenReturn(mockStudyJoin);
            when(mockStudyJoin.isSameStudy(mockStudy)).thenReturn(false);

            // When & Then
            MosException exception = assertThrows(MosException.class, () -> {
                studyJoinService.cancelStudyJoin(studyId, studyJoinId);
            });

            assertEquals(StudyJoinErrorCode.STUDY_JOIN_MISMATCH, exception.getErrorCode());

            verify(entityFacade).getStudy(studyId);
            verify(entityFacade).getStudyJoin(studyJoinId);
            verify(mockStudyJoin).isSameStudy(mockStudy);
            verify(mockStudyJoin, never()).cancel();
        }

        @Test
        @DisplayName("스터디 신청 상태가 PENDING이 아닐 때 MosException 발생")
        void deleteStudyJoin_NotPending() {
            // Given
            Long studyId = 1L;
            Long studyJoinId = 1L;
            Study mockStudy = mock(Study.class);
            StudyJoin mockStudyJoin = mock(StudyJoin.class);

            when(entityFacade.getStudy(studyId)).thenReturn(mockStudy);
            when(entityFacade.getStudyJoin(studyJoinId)).thenReturn(mockStudyJoin);
            when(mockStudyJoin.isSameStudy(mockStudy)).thenReturn(true);
            when(mockStudyJoin.isPending()).thenReturn(false);

            // When & Then
            MosException exception = assertThrows(MosException.class, () -> {
                studyJoinService.cancelStudyJoin(studyId, studyJoinId);
            });

            assertEquals(StudyJoinErrorCode.STUDY_JOIN_NOT_PENDING, exception.getErrorCode());

            verify(entityFacade).getStudy(studyId);
            verify(entityFacade).getStudyJoin(studyJoinId);
            verify(mockStudyJoin).isSameStudy(mockStudy);
            verify(mockStudyJoin).isPending();
            verify(mockStudyJoin, never()).cancel();
        }
    }
}

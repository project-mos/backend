package com.mos.backend.studyschedules.application;

import com.mos.backend.common.exception.MosException;
import com.mos.backend.common.infrastructure.EntityFacade;
import com.mos.backend.studies.entity.Study;
import com.mos.backend.studies.entity.exception.StudyErrorCode;
import com.mos.backend.studycurriculum.entity.StudyCurriculum;
import com.mos.backend.studycurriculum.infrastructure.StudyCurriculumRepository;
import com.mos.backend.studymembers.application.StudyMemberService;
import com.mos.backend.studyschedulecurriculums.entity.StudyScheduleCurriculum;
import com.mos.backend.studyschedulecurriculums.infrastructure.StudyScheduleCurriculumRepository;
import com.mos.backend.studyschedules.entity.StudySchedule;
import com.mos.backend.studyschedules.infrastructure.StudyScheduleRepository;
import com.mos.backend.studyschedules.presentation.req.StudyScheduleCreateReq;
import com.mos.backend.studyschedules.presentation.req.StudyScheduleUpdateReq;
import com.mos.backend.users.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("StudyScheduleService 테스트")
class StudyScheduleServiceTest {
    @Mock
    private StudyScheduleRepository studyScheduleRepository;
    @Mock
    private StudyCurriculumRepository studyCurriculumRepository;
    @Mock
    private EntityFacade entityFacade;
    @Mock
    private StudyMemberService studyMemberService;
    @Mock
    private StudyScheduleCurriculumRepository studyScheduleCurriculumRepository;
    @InjectMocks
    private StudyScheduleService studyScheduleService;

    @Nested
    @DisplayName("스터디 일정 생성 성공 시나리오")
    class CreateStudyScheduleSuccessScenario {
        @Test
        @DisplayName("스터디 일정 생성 성공")
        void createStudySchedule_Success() {
            // Given
            Long userId = 1L;
            Long studyId = 1L;
            List<Long> curriculumIds = List.of(1L, 2L);

            User mockUser = mock(User.class);
            Study mockStudy = mock(Study.class);
            StudyScheduleCreateReq req = mock(StudyScheduleCreateReq.class);
            StudyCurriculum mockCurriculum = mock(StudyCurriculum.class);

            when(entityFacade.getUser(userId)).thenReturn(mockUser);
            when(entityFacade.getStudy(studyId)).thenReturn(mockStudy);
            when(req.getCurriculumIds()).thenReturn(curriculumIds);
            when(entityFacade.getStudyCurriculum(anyLong())).thenReturn(mockCurriculum);
            when(mockCurriculum.getStudy()).thenReturn(mockStudy);
            when(mockStudy.getId()).thenReturn(studyId);
            when(mockStudy.isRelated(studyId)).thenReturn(true);
            when(mockCurriculum.getStudy()).thenReturn(mockStudy);
            when(mockStudy.getId()).thenReturn(studyId);
            when(mockStudy.isRelated(studyId)).thenReturn(true);

            // When
            studyScheduleService.createStudySchedule(userId, studyId, req);

            // Then
            verify(entityFacade).getUser(userId);
            verify(entityFacade).getStudy(studyId);
            verify(req).getCurriculumIds();
            verify(entityFacade, times(curriculumIds.size())).getStudyCurriculum(anyLong());
            verify(studyScheduleCurriculumRepository, times(curriculumIds.size())).save(any(StudyScheduleCurriculum.class));
            verify(studyScheduleRepository).save(any(StudySchedule.class));
        }
    }

    @Nested
    @DisplayName("스터디 일정 생성 실패 시나리오")
    class CreateStudyScheduleFailScenario {
        @Test
        @DisplayName("스터디 커리큘럼이 스터디와 일치하지 않는 경우 MosException 발생")
        void createStudyScheduleByCurriculum_Fail() {
            // Given
            Long userId = 1L;
            Long studyId = 1L;
            List<Long> curriculumIds = List.of(1L, 2L);

            User mockUser = mock(User.class);
            Study mockStudy = mock(Study.class);
            StudyScheduleCreateReq req = mock(StudyScheduleCreateReq.class);
            StudyCurriculum mockCurriculum = mock(StudyCurriculum.class);

            when(entityFacade.getUser(userId)).thenReturn(mockUser);
            when(entityFacade.getStudy(studyId)).thenReturn(mockStudy);
            when(entityFacade.getStudyCurriculum(anyLong())).thenReturn(mockCurriculum);
            when(req.getCurriculumIds()).thenReturn(curriculumIds);
            when(mockCurriculum.getStudy()).thenReturn(mockStudy);
            when(mockStudy.getId()).thenReturn(studyId);
            when(mockStudy.isRelated(studyId)).thenReturn(false);

            // When & Then
            MosException exception = assertThrows(MosException.class,
                    () -> studyScheduleService.createStudySchedule(userId, studyId, req)
            );

            assertEquals(StudyErrorCode.UNRELATED_STUDY, exception.getErrorCode());
            verify(entityFacade).getUser(userId);
            verify(entityFacade).getStudy(studyId);
            verify(req).getCurriculumIds();
            verify(studyScheduleRepository).save(any(StudySchedule.class));
            verify(entityFacade, times(1)).getStudyCurriculum(anyLong());
        }
    }

    @Nested
    @DisplayName("스터디 일정 조회 성공 시나리오")
    class GetStudyScheduleSuccessScenario {
        @Test
        @DisplayName("내 스터디 일정 조회 성공")
        void getMyStudySchedules_Success() {
            // Given
            Long userId = 1L;
            User user = mock(User.class);
            Study study = mock(Study.class);
            StudySchedule studySchedule = mock(StudySchedule.class);
            List<StudySchedule> studySchedules = List.of(studySchedule);
            List<StudyCurriculum> studyCurriculums = List.of(mock(StudyCurriculum.class));

            when(entityFacade.getUser(userId)).thenReturn(user);
            when(user.getId()).thenReturn(userId);
            when(studyScheduleRepository.findAllByActivatedUserId(userId)).thenReturn(studySchedules);
            when(studySchedule.getStudy()).thenReturn(study);
            when(studySchedule.getId()).thenReturn(1L);
            when(studyCurriculumRepository.findAllByStudyScheduleId(studySchedule.getId())).thenReturn(studyCurriculums);

            // When
            studyScheduleService.getMyStudySchedules(userId);

            // Then
            verify(entityFacade).getUser(userId);
            verify(studyScheduleRepository).findAllByActivatedUserId(userId);
            verify(studyCurriculumRepository, times(studySchedules.size())).findAllByStudyScheduleId(anyLong());
        }

        @Test
        @DisplayName("스터디 일정 조회 성공")
        void getStudySchedules_Success() {
            // Given
            Long userId = 1L;
            Long studyId = 1L;
            Study study = mock(Study.class);
            StudySchedule studySchedule = mock(StudySchedule.class);
            List<StudySchedule> studySchedules = List.of(studySchedule);
            List<StudyCurriculum> studyCurriculums = List.of(mock(StudyCurriculum.class));

            when(entityFacade.getUser(userId)).thenReturn(mock(User.class));
            when(entityFacade.getStudy(studyId)).thenReturn(study);
            when(study.getId()).thenReturn(studyId);
            when(studyScheduleRepository.findByStudyId(studyId)).thenReturn(studySchedules);
            when(studySchedule.getStudy()).thenReturn(study);
            when(studySchedule.getId()).thenReturn(1L);
            when(studyCurriculumRepository.findAllByStudyScheduleId(studySchedule.getId())).thenReturn(studyCurriculums);

            // When
            studyScheduleService.getStudySchedules(userId, studyId);

            // Then
            verify(entityFacade).getUser(userId);
            verify(entityFacade).getStudy(studyId);
            verify(studyScheduleRepository).findByStudyId(studyId);
            verify(studyCurriculumRepository, times(studySchedules.size())).findAllByStudyScheduleId(anyLong());
        }
    }

    @Nested
    @DisplayName("스터디 일정 수정 성공 시나리오")
    class UpdateStudyScheduleSuccessScenario {
        @Test
        @DisplayName("스터디 일정 수정 성공")
        void updateStudySchedule_Success() {
            // Given
            Long userId = 1L;
            Long studyId = 1L;
            Long studyScheduleId = 1L;
            Study study = mock(Study.class);
            StudySchedule studySchedule = mock(StudySchedule.class);

            when(entityFacade.getUser(userId)).thenReturn(mock(User.class));
            when(entityFacade.getStudy(studyId)).thenReturn(study);
            when(entityFacade.getStudySchedule(studyScheduleId)).thenReturn(studySchedule);
            when(studySchedule.getStudy()).thenReturn(study);
            when(study.getId()).thenReturn(studyId);
            when(study.isRelated(studyId)).thenReturn(true);

            // When
            studyScheduleService.updateStudySchedule(userId, studyId, studyScheduleId, mock(StudyScheduleUpdateReq.class));

            // Then
            verify(entityFacade).getUser(userId);
            verify(entityFacade).getStudy(studyId);
            verify(entityFacade).getStudySchedule(studyScheduleId);
            verify(studySchedule).update(any(), any(), any(), any());
        }
    }

    @Nested
    @DisplayName("스터디 일정 수정 실패 시나리오")
    class UpdateStudyScheduleFailScenario {
        @Test
        @DisplayName("스터디 일정 수정 실패 - 스터디와 스터디 일정이 일치하지 않는 경우 MosException 발생")
        void updateStudySchedule_Fail() {
            // Given
            Long userId = 1L;
            Long studyId = 1L;
            Long studyScheduleId = 1L;
            Study study = mock(Study.class);
            StudySchedule studySchedule = mock(StudySchedule.class);

            when(entityFacade.getUser(userId)).thenReturn(mock(User.class));
            when(entityFacade.getStudy(studyId)).thenReturn(study);
            when(entityFacade.getStudySchedule(studyScheduleId)).thenReturn(studySchedule);
            when(studySchedule.getStudy()).thenReturn(study);
            when(study.getId()).thenReturn(studyId);
            when(study.isRelated(studyId)).thenReturn(false);

            // When & Then
            MosException exception = assertThrows(MosException.class,
                    () -> studyScheduleService.updateStudySchedule(userId, studyId, studyScheduleId, mock(StudyScheduleUpdateReq.class))
            );

            assertEquals(exception.getErrorCode(), StudyErrorCode.UNRELATED_STUDY);
            verify(entityFacade).getUser(userId);
            verify(entityFacade).getStudy(studyId);
            verify(entityFacade).getStudySchedule(studyScheduleId);
            verify(studySchedule, never()).update(any(), any(), any(), any());
        }
    }

    @Nested
    @DisplayName("스터디 일정 삭제 성공 시나리오")
    class DeleteStudyScheduleSuccessScenario {
        @Test
        @DisplayName("스터디 일정 삭제 성공")
        void deleteStudySchedule_Success() {
            // Given
            Long userId = 1L;
            Long studyId = 1L;
            Long studyScheduleId = 1L;
            Study study = mock(Study.class);
            StudySchedule studySchedule = mock(StudySchedule.class);

            when(entityFacade.getUser(userId)).thenReturn(mock(User.class));
            when(entityFacade.getStudy(studyId)).thenReturn(study);
            when(entityFacade.getStudySchedule(studyScheduleId)).thenReturn(studySchedule);
            when(studySchedule.getStudy()).thenReturn(study);
            when(study.getId()).thenReturn(studyId);
            when(study.isRelated(studyId)).thenReturn(true);

            // When
            studyScheduleService.deleteStudySchedule(userId, studyId, studyScheduleId);

            // Then
            verify(entityFacade).getUser(userId);
            verify(entityFacade).getStudy(studyId);
            verify(entityFacade).getStudySchedule(studyScheduleId);
            verify(studyScheduleRepository).delete(studySchedule);
        }
    }

    @Nested
    @DisplayName("스터디 일정 삭제 실패 시나리오")
    class DeleteStudyScheduleFailScenario {
        @Test
        @DisplayName("스터디 일정 삭제 실패 - 스터디와 스터디 일정이 일치하지 않는 경우 MosException 발생")
        void deleteStudySchedule_Fail() {
            // Given
            Long userId = 1L;
            Long studyId = 1L;
            Long studyScheduleId = 1L;
            Study study = mock(Study.class);
            StudySchedule studySchedule = mock(StudySchedule.class);

            when(entityFacade.getUser(userId)).thenReturn(mock(User.class));
            when(entityFacade.getStudy(studyId)).thenReturn(study);
            when(entityFacade.getStudySchedule(studyScheduleId)).thenReturn(studySchedule);
            when(studySchedule.getStudy()).thenReturn(study);
            when(study.getId()).thenReturn(studyId);
            when(study.isRelated(studyId)).thenReturn(false);

            // When & Then
            MosException exception = assertThrows(MosException.class,
                    () -> studyScheduleService.deleteStudySchedule(userId, studyId, studyScheduleId)
            );

            assertEquals(exception.getErrorCode(), StudyErrorCode.UNRELATED_STUDY);
            verify(entityFacade).getUser(userId);
            verify(entityFacade).getStudy(studyId);
            verify(entityFacade).getStudySchedule(studyScheduleId);
            verify(studyScheduleRepository, never()).delete(studySchedule);
        }
    }
}

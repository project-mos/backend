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
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("StudyApplicationService 테스트")
public class StudyApplicationServiceTest {

    @Mock
    private StudyMemberRepository studyMemberRepository;
    @Mock
    private EntityFacade entityFacade;
    @InjectMocks
    private StudyApplicationService studyApplicationService;


    @Nested
    @DisplayName("스터디 신청 승인 성공 시나리오")
    class ApproveSuccessScenarios {
        @Test
        @DisplayName("스터디 신청 승인")
        void approveStudyMember_Success() {
            // Given
            Long userId = 1L;
            Long studyApplicationId = 1L;
            User mockUser = mock(User.class);
            Study mockStudy = mock(Study.class);
            StudyApplication mockStudyApplication = mock(StudyApplication.class);

            when(entityFacade.getUser(userId)).thenReturn(mockUser);
            when(entityFacade.getStudyApplication(studyApplicationId)).thenReturn(mockStudyApplication);
            when(mockStudyApplication.getStudy()).thenReturn(mockStudy);
            when(studyMemberRepository.countByStudy(mockStudy)).thenReturn(1L);
            when(mockStudy.getMaxStudyMemberCount()).thenReturn(5);

            // When
            studyApplicationService.approveStudyApplication(userId, studyApplicationId);

            // Then
            verify(entityFacade).getUser(userId);
            verify(entityFacade).getStudyApplication(studyApplicationId);
            verify(mockStudyApplication).approve();
            verify(studyMemberRepository).save(any(StudyMember.class));
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
            Long studyApplicationId = 1L;
            User mockUser = mock(User.class);
            Study mockStudy = mock(Study.class);
            StudyApplication mockStudyApplication = mock(StudyApplication.class);

            when(entityFacade.getUser(userId)).thenReturn(mockUser);
            when(entityFacade.getStudyApplication(studyApplicationId)).thenReturn(mockStudyApplication);
            when(mockStudyApplication.getStudy()).thenReturn(mockStudy);
            when(studyMemberRepository.countByStudy(mockStudy)).thenReturn(0L);

            // When & Then
            MosException exception = assertThrows(MosException.class, () -> {
                studyApplicationService.approveStudyApplication(userId, studyApplicationId);
            });

            assertEquals(StudyErrorCode.STUDY_NOT_FOUND, exception.getErrorCode());

            verify(entityFacade).getUser(userId);
            verify(entityFacade).getStudyApplication(studyApplicationId);
            verify(mockStudyApplication, never()).approve();
            verify(studyMemberRepository, never()).save(any(StudyMember.class));
        }

        @Test
        @DisplayName("스터디 멤버가 가득 찼을 때 MosException 발생")
        void approveStudyMember_StudyMemberFull() {
            // Given
            Long userId = 1L;
            Long studyApplicationId = 1L;
            User mockUser = mock(User.class);
            Study mockStudy = mock(Study.class);
            StudyApplication mockStudyApplication = mock(StudyApplication.class);

            when(entityFacade.getUser(userId)).thenReturn(mockUser);
            when(entityFacade.getStudyApplication(studyApplicationId)).thenReturn(mockStudyApplication);
            when(mockStudyApplication.getStudy()).thenReturn(mockStudy);
            when(studyMemberRepository.countByStudy(mockStudy)).thenReturn(4L);
            when(mockStudy.getMaxStudyMemberCount()).thenReturn(4);

            // When & Then
            MosException exception = assertThrows(MosException.class, () -> {
                studyApplicationService.approveStudyApplication(userId, studyApplicationId);
            });

            assertEquals(StudyMemberErrorCode.STUDY_MEMBER_FULL, exception.getErrorCode());

            verify(entityFacade).getUser(userId);
            verify(entityFacade).getStudyApplication(studyApplicationId);
            verify(mockStudyApplication, never()).approve();
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
            Long userId = 1L;
            Long studyApplicationId = 1L;
            User mockUser = mock(User.class);
            StudyApplication mockStudyApplication = mock(StudyApplication.class);

            when(entityFacade.getUser(userId)).thenReturn(mockUser);
            when(entityFacade.getStudyApplication(studyApplicationId)).thenReturn(mockStudyApplication);

            // When
            studyApplicationService.rejectStudyApplication(userId, studyApplicationId);

            // Then
            verify(entityFacade).getUser(userId);
            verify(entityFacade).getStudyApplication(studyApplicationId);
            verify(mockStudyApplication).reject();
        }
    }
}

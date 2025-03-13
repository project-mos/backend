package com.mos.backend.studycurriculum.application;

import com.mos.backend.common.exception.MosException;
import com.mos.backend.common.infrastructure.EntityFacade;
import com.mos.backend.studies.entity.Study;
import com.mos.backend.studies.entity.exception.StudyErrorCode;
import com.mos.backend.studies.infrastructure.StudyRepository;
import com.mos.backend.studymembers.application.StudyMemberService;
import com.mos.backend.studymembers.entity.StudyMember;
import com.mos.backend.studymembers.infrastructure.StudyMemberRepository;
import com.mos.backend.users.entity.User;
import com.mos.backend.users.entity.exception.UserErrorCode;
import com.mos.backend.users.infrastructure.respository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("StudyCurriculumService 테스트")
class StudyCurriculumServiceTest {
    @Mock
    private StudyRepository studyRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private StudyMemberRepository studyMemberRepository;

    @Mock
    private EntityFacade entityFacade;

    @InjectMocks
    private StudyMemberService studyMemberService;

    @Nested
    @DisplayName("스터디 멤버 생성 성공 시나리오")
    class SuccessScenarios {
        @Test
        @DisplayName("정상적인 스터디 멤버 생성")
        void createStudyMember_Success() {
            // Given
            Long studyId = 1L;
            Long userId = 1L;
            Study mockStudy = mock(Study.class);
            User mockUser = mock(User.class);

            when(entityFacade.getStudy(studyId)).thenReturn(mockStudy);
            when(entityFacade.getUser(userId)).thenReturn(mockUser);

            // When
            studyMemberService.create(studyId, userId);

            // Then
            verify(entityFacade).getStudy(studyId);
            verify(entityFacade).getUser(userId);
            verify(studyMemberRepository).save(any(StudyMember.class));
        }
    }

    @Nested
    @DisplayName("스터디 멤버 생성 실패 시나리오")
    class FailureScenarios {
        @Test
        @DisplayName("스터디가 존재하지 않을 때 MosException 발생")
        void createStudyMember_StudyNotFound() {
            // Given
            Long studyId = 1L;
            Long userId = 1L;

            when(entityFacade.getStudy(studyId)).thenThrow(new MosException(StudyErrorCode.STUDY_NOT_FOUND));

            // When & Then
            MosException exception = assertThrows(MosException.class, () -> {
                studyMemberService.create(studyId, userId);
            });

            assertEquals(StudyErrorCode.STUDY_NOT_FOUND, exception.getErrorCode());
            verify(entityFacade, never()).getUser(userId);
            verify(entityFacade).getStudy(studyId);
            verify(studyMemberRepository, never()).save(any(StudyMember.class));
        }

        @Test
        @DisplayName("유저가 존재하지 않을 때 MosException 발생")
        void createStudyMember_UserNotFound() {
            // Given
            Long studyId = 1L;
            Long userId = 1L;
            Study mockStudy = mock(Study.class);

            when(entityFacade.getStudy(studyId)).thenReturn(mockStudy);
            when(entityFacade.getUser(userId)).thenThrow(new MosException(UserErrorCode.USER_NOT_FOUND));

            // When & Then
            MosException exception = assertThrows(MosException.class, () -> {
                studyMemberService.create(studyId, userId);
            });

            assertEquals(UserErrorCode.USER_NOT_FOUND, exception.getErrorCode());
            verify(entityFacade).getStudy(studyId);
            verify(entityFacade).getUser(userId);
            verify(studyMemberRepository, never()).save(any(StudyMember.class));
        }
    }
}

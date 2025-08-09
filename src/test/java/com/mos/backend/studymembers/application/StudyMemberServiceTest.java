package com.mos.backend.studymembers.application;

import com.mos.backend.attendances.entity.Attendance;
import com.mos.backend.attendances.infrastructure.AttendanceRepository;
import com.mos.backend.common.exception.MosException;
import com.mos.backend.common.infrastructure.EntityFacade;
import com.mos.backend.studies.entity.Study;
import com.mos.backend.studies.entity.exception.StudyErrorCode;
import com.mos.backend.studies.infrastructure.StudyRepository;
import com.mos.backend.studymembers.entity.ParticipationStatus;
import com.mos.backend.studymembers.entity.StudyMember;
import com.mos.backend.studymembers.entity.StudyMemberRoleType;
import com.mos.backend.studymembers.entity.exception.StudyMemberErrorCode;
import com.mos.backend.studymembers.infrastructure.StudyMemberRepository;
import com.mos.backend.users.entity.User;
import com.mos.backend.users.infrastructure.respository.UserRepository;
import com.mos.backend.userstudysettings.application.UserStudySettingService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("StudyMemberService 테스트")
class StudyMemberServiceTest {
    @Mock
    private StudyRepository studyRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private StudyMemberRepository studyMemberRepository;
    @Mock
    private AttendanceRepository attendanceRepository;
    @Mock
    private EntityFacade entityFacade;
    @Mock
    private UserStudySettingService userStudySettingService;
    @InjectMocks
    private StudyMemberService studyMemberService;

    @Nested
    @DisplayName("스터디 리더 생성 성공 시나리오")
    class CreateLeaderSuccessScenarios {
        @Test
        @DisplayName("스터디 리더 생성 성공")
        void createStudyLeader_Success() {
            // Given
            Long studyId = 1L;
            Long userId = 1L;
            Study mockStudy = mock(Study.class);
            User mockUser = mock(User.class);

            when(entityFacade.getStudy(studyId)).thenReturn(mockStudy);
            when(entityFacade.getUser(userId)).thenReturn(mockUser);
            when(studyMemberRepository.existsByStudyAndRoleType(mockStudy, StudyMemberRoleType.LEADER)).thenReturn(false);

            // When
            studyMemberService.createStudyLeader(studyId, userId);

            // Then
            verify(entityFacade).getStudy(studyId);
            verify(entityFacade).getUser(userId);
            verify(studyMemberRepository).save(any(StudyMember.class));
        }
    }

    @Nested
    @DisplayName("스터디 리더 생성 실패 시나리오")
    class CreateLeaderFailureScenarios {
        @Test
        @DisplayName("스터디 리더가 이미 존재할 때 MosException 발생")
        void createStudyLeader_StudyLeaderAlreadyExist() {
            // Given
            Long studyId = 1L;
            Long userId = 1L;
            Study mockStudy = mock(Study.class);
            User mockUser = mock(User.class);

            when(entityFacade.getStudy(studyId)).thenReturn(mockStudy);
            when(entityFacade.getUser(userId)).thenReturn(mockUser);
            when(studyMemberRepository.existsByStudyAndRoleType(mockStudy, StudyMemberRoleType.LEADER)).thenReturn(true);

            // When
            MosException exception = assertThrows(MosException.class, () -> {
                studyMemberService.createStudyLeader(studyId, userId);
            });

            // Then
            assertEquals(StudyMemberErrorCode.STUDY_LEADER_ALREADY_EXIST, exception.getErrorCode());
            verify(entityFacade).getStudy(studyId);
            verify(entityFacade).getUser(userId);
            verify(studyMemberRepository, never()).save(any(StudyMember.class));
        }
    }

    @Nested
    @DisplayName("스터디원 생성 성공 시나리오")
    class CreateSuccessScenarios {
        @Test
        @DisplayName("스터디원 생성 성공")
        void createStudyMember_Success() {
            // Given
            Long studyId = 1L;
            Long userId = 1L;
            Study mockStudy = mock(Study.class);
            User mockUser = mock(User.class);
            List<ParticipationStatus> currentParticipationStatusList = Arrays.asList(ParticipationStatus.ACTIVATED, ParticipationStatus.COMPLETED);

            when(entityFacade.getStudy(studyId)).thenReturn(mockStudy);
            when(entityFacade.getUser(userId)).thenReturn(mockUser);
            when(studyMemberRepository.countByStudyAndStatusIn(mockStudy, currentParticipationStatusList)).thenReturn(1);
            when(mockStudy.getMaxStudyMemberCount()).thenReturn(5);

            // When
            studyMemberService.createStudyMember(studyId, userId);

            // Then
            verify(entityFacade, times(2)).getStudy(studyId);
            verify(entityFacade).getUser(userId);
            verify(studyMemberRepository).save(any(StudyMember.class));
        }
    }

    @Nested
    @DisplayName("스터디원 생성 실패 시나리오")
    class CreateFailureScenarios {
        @Test
        @DisplayName("스터디가 존재하지 않을 때 MosException 발생")
        void createStudyMember_StudyNotFound() {
            // Given
            Long studyId = 1L;
            Long userId = 1L;

            when(entityFacade.getStudy(studyId)).thenThrow(new MosException(StudyErrorCode.STUDY_NOT_FOUND));

            // When & Then
            MosException exception = assertThrows(MosException.class, () -> {
                studyMemberService.createStudyLeader(studyId, userId);
            });

            assertEquals(StudyErrorCode.STUDY_NOT_FOUND, exception.getErrorCode());
            verify(entityFacade).getStudy(studyId);
            verify(entityFacade, never()).getUser(anyLong());
            verify(studyMemberRepository, never()).save(any(StudyMember.class));
        }

        @Test
        @DisplayName("스터디 멤버 수가 0일 때 MosException 발생")
        void createStudyMember_StudyMemberCountZero() {
            // Given
            Long studyId = 1L;
            Long userId = 1L;
            Study mockStudy = mock(Study.class);
            User mockUser = mock(User.class);
            List<ParticipationStatus> currentParticipationStatusList = Arrays.asList(ParticipationStatus.ACTIVATED, ParticipationStatus.COMPLETED);

            when(entityFacade.getStudy(studyId)).thenReturn(mockStudy);
            when(entityFacade.getUser(userId)).thenReturn(mockUser);
            when(studyMemberRepository.countByStudyAndStatusIn(mockStudy, currentParticipationStatusList)).thenReturn(0);

            // When & Then
            MosException exception = assertThrows(MosException.class, () -> {
                studyMemberService.createStudyMember(studyId, userId);
            });

            assertEquals(StudyErrorCode.STUDY_NOT_FOUND, exception.getErrorCode());
            verify(entityFacade, times(2)).getStudy(studyId);
            verify(entityFacade).getUser(userId);
            verify(studyMemberRepository, never()).save(any(StudyMember.class));
        }

        @Test
        @DisplayName("스터디 멤버 수가 최대 스터디 멤버 수 이상일 때 MosException 발생")
        void createStudyMember_StudyMemberCountOver() {
            // Given
            Long studyId = 1L;
            Long userId = 1L;
            Study mockStudy = mock(Study.class);
            User mockUser = mock(User.class);
            List<ParticipationStatus> currentParticipationStatusList = Arrays.asList(ParticipationStatus.ACTIVATED, ParticipationStatus.COMPLETED);

            when(entityFacade.getStudy(studyId)).thenReturn(mockStudy);
            when(entityFacade.getUser(userId)).thenReturn(mockUser);
            when(studyMemberRepository.countByStudyAndStatusIn(mockStudy, currentParticipationStatusList)).thenReturn(5);
            when(mockStudy.getMaxStudyMemberCount()).thenReturn(5);

            // When & Then
            MosException exception = assertThrows(MosException.class, () -> {
                studyMemberService.createStudyMember(studyId, userId);
            });

            assertEquals(StudyMemberErrorCode.STUDY_MEMBER_FULL, exception.getErrorCode());
            verify(entityFacade, times(2)).getStudy(studyId);
            verify(entityFacade).getUser(userId);
            verify(studyMemberRepository, never()).save(any(StudyMember.class));
        }

        @Test
        @DisplayName("이미 스터디에 참여 중인 유저일 때 MosException 발생")
        void createStudyMember_UserAlreadyInStudy() {
            // Given
            Long studyId = 1L;
            Long userId = 1L;
            Study mockStudy = mock(Study.class);
            User mockUser = mock(User.class);

            when(entityFacade.getStudy(studyId)).thenReturn(mockStudy);
            when(entityFacade.getUser(userId)).thenReturn(mockUser);
            when(studyMemberRepository.countByStudyAndStatusIn(mockStudy, List.of(ParticipationStatus.ACTIVATED, ParticipationStatus.COMPLETED))).thenReturn(1);
            when(mockStudy.getMaxStudyMemberCount()).thenReturn(5);
            when(studyMemberRepository.existsByUserAndStudy(mockUser, mockStudy)).thenReturn(true);

            // When & Then
            MosException exception = assertThrows(MosException.class, () -> {
                studyMemberService.createStudyMember(studyId, userId);
            });

            assertEquals(StudyMemberErrorCode.CONFLICT, exception.getErrorCode());
            verify(entityFacade, times(2)).getStudy(studyId);
            verify(entityFacade).getUser(userId);
            verify(studyMemberRepository, never()).save(any(StudyMember.class));
        }
    }

    @Nested
    @DisplayName("스터디원 조회 성공 시나리오")
    class GetStudyMembersSuccessScenarios {
        @Test
        @DisplayName("스터디원 조회 성공")
        void getStudyMembers_Success() {
            // Given
            Long userId = 1L;
            Long studyId = 1L;
            Long studyMemberId = 1L;
            User user = mock(User.class);
            Study study = mock(Study.class);
            StudyMember studyMember = mock(StudyMember.class);
            List<StudyMember> studyMembers = List.of(studyMember);
            Attendance attendance = mock(Attendance.class);
            List<Attendance> attendances = List.of(attendance);

            when(entityFacade.getStudy(studyId)).thenReturn(study);
            when(study.getId()).thenReturn(studyId);
            when(studyMember.getId()).thenReturn(studyMemberId);
            when(studyMemberRepository.findAllByStudyId(studyId)).thenReturn(studyMembers);
            when(attendanceRepository.findAllByStudyMemberId(studyMemberId)).thenReturn(attendances);
            when(attendance.getModifiedAt()).thenReturn(LocalDateTime.now());
            when(studyMember.getUser()).thenReturn(user);
            when(user.getId()).thenReturn(userId);
            when(studyMember.getRoleType()).thenReturn(StudyMemberRoleType.LEADER);

            // When
            studyMemberService.getStudyMembers(studyId);

            // Then
            verify(entityFacade).getStudy(studyId);
            verify(studyMemberRepository).findAllByStudyId(studyId);
            verify(attendanceRepository).findAllByStudyMemberId(studyMemberId);
        }
    }

    @Nested
    @DisplayName("스터디장 위임 성공 시나리오")
    class DelegateLeaderSuccessScenarios {
        @Test
        @DisplayName("스터디장 위임 성공")
        void delegateLeader_Success() {
            // Given
            Long userId = 1L;
            Long studyId = 1L;
            Long studyMemberId = 1L;
            User user = mock(User.class);
            Study study = mock(Study.class);
            StudyMember studyLeader = mock(StudyMember.class);
            StudyMember studyMember = mock(StudyMember.class);

            when(entityFacade.getUser(userId)).thenReturn(user);
            when(entityFacade.getStudy(studyId)).thenReturn(study);
            when(entityFacade.getStudyMember(studyMemberId)).thenReturn(studyMember);
            when(user.getId()).thenReturn(userId);
            when(study.getId()).thenReturn(studyId);
            when(studyMemberRepository.findByUserIdAndStudyId(userId, studyId)).thenReturn(Optional.of(studyLeader));

            // When
            studyMemberService.delegateLeader(userId, studyId, studyMemberId);

            // Then
            verify(entityFacade).getUser(userId);
            verify(entityFacade).getStudy(studyId);
            verify(entityFacade).getStudyMember(studyMemberId);
            verify(studyMemberRepository).findByUserIdAndStudyId(userId, studyId);
            verify(studyLeader).changeToMember();
            verify(studyMember).changeToLeader();
        }
    }

    @Nested
    @DisplayName("스터디장 위임 실패 시나리오")
    class DelegateLeaderFailureScenarios {
        @Test
        @DisplayName("스터디장이 조회되지 않는 경우 MosException 발생")
        void delegateLeader_NotLeader() {
            // Given
            Long userId = 1L;
            Long studyId = 1L;
            Long studyMemberId = 1L;
            User user = mock(User.class);
            Study study = mock(Study.class);
            StudyMember studyMember = mock(StudyMember.class);
            StudyMember studyLeader = mock(StudyMember.class);

            when(entityFacade.getUser(userId)).thenReturn(user);
            when(entityFacade.getStudy(studyId)).thenReturn(study);
            when(entityFacade.getStudyMember(studyMemberId)).thenReturn(studyMember);
            when(user.getId()).thenReturn(userId);
            when(study.getId()).thenReturn(studyId);
            when(studyMemberRepository.findByUserIdAndStudyId(userId, studyId)).thenReturn(Optional.empty());

            // When
            MosException exception = assertThrows(MosException.class, () -> {
                studyMemberService.delegateLeader(userId, studyId, studyMemberId);
            });

            // Then
            assertEquals(StudyMemberErrorCode.STUDY_MEMBER_NOT_FOUND, exception.getErrorCode());
            verify(studyLeader, never()).changeToMember();
            verify(studyMember, never()).changeToLeader();
        }
    }

    @Nested
    @DisplayName("스터디 탈퇴 성공 시나리오")
    class WithDrawSuccessScenarios {
        @Test
        @DisplayName("스터디 탈퇴 성공")
        void withDraw_Success() {
            // Given
            Long userId = 1L;
            Long studyId = 1L;
            User user = mock(User.class);
            Study study = mock(Study.class);
            StudyMember studyMember = mock(StudyMember.class);

            when(entityFacade.getUser(userId)).thenReturn(user);
            when(entityFacade.getStudy(studyId)).thenReturn(study);
            when(studyMemberRepository.findByUserIdAndStudyId(userId, studyId)).thenReturn(Optional.of(studyMember));
            when(studyMember.isLeader()).thenReturn(false);

            // When
            studyMemberService.withDraw(userId, studyId);

            // Then
            verify(entityFacade).getUser(userId);
            verify(entityFacade).getStudy(studyId);
            verify(studyMemberRepository).findByUserIdAndStudyId(userId, studyId);
            verify(studyMember).withDrawStudy();
        }
    }

    @Nested
    @DisplayName("스터디 탈퇴 실패 시나리오")
    class WithDrawFailureScenarios {
        @Test
        @DisplayName("스터디장이 탈퇴하려고 할 때 MosException 발생")
        void withDraw_LeaderWithDrawForbidden() {
            // Given
            Long userId = 1L;
            Long studyId = 1L;
            User user = mock(User.class);
            Study study = mock(Study.class);
            StudyMember studyMember = mock(StudyMember.class);

            when(entityFacade.getUser(userId)).thenReturn(user);
            when(entityFacade.getStudy(studyId)).thenReturn(study);
            when(studyMemberRepository.findByUserIdAndStudyId(userId, studyId)).thenReturn(Optional.of(studyMember));
            when(studyMember.isLeader()).thenReturn(true);

            // When
            MosException exception = assertThrows(MosException.class, () -> {
                studyMemberService.withDraw(userId, studyId);
            });

            // Then
            assertEquals(StudyMemberErrorCode.STUDY_LEADER_WITHDRAW_FORBIDDEN, exception.getErrorCode());
            verify(studyMember, never()).withDrawStudy();
        }
    }
}

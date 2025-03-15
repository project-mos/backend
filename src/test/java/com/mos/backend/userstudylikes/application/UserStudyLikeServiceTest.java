package com.mos.backend.userstudylikes.application;

import com.mos.backend.common.infrastructure.EntityFacade;
import com.mos.backend.hotstudies.application.HotStudyService;
import com.mos.backend.studies.entity.Study;
import com.mos.backend.users.entity.User;
import com.mos.backend.userstudylikes.entity.UserStudyLike;
import com.mos.backend.userstudylikes.infrastructure.UserStudyLikeRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserStudyLikeServiceTest {

    @Mock
    private UserStudyLikeRepository userStudyLikeRepository;

    @Mock
    private EntityFacade entityFacade;

    @Mock
    private HotStudyService hotStudyService;

    @InjectMocks
    private UserStudyLikeService userStudyLikeService;

    @Nested
    @DisplayName("좋아요 기능")
    class LikeTest {

        @Test
        @DisplayName("좋아요 성공")
        void like() {

            // given
            Long studyId = 1L;
            Long userId = 1L;

            Study study = Study.builder().id(studyId).build();
            User user = User.builder().id(userId).build();

            when(entityFacade.getStudy(studyId)).thenReturn(study);
            when(entityFacade.getUser(userId)).thenReturn(user);
            when(userStudyLikeRepository.existsUserStudyLikeByUserAndStudy(user, study)).thenReturn(false);

            // when
            userStudyLikeService.like(studyId, userId);

            // then
            verify(userStudyLikeRepository).save(any(UserStudyLike.class));
        }

        @Test
        @DisplayName("좋아요 실패")
        void alreadyLiked_like_fail() {
            // given
            Long studyId = 1L;
            Long userId = 1L;

            Study study = Study.builder().id(studyId).build();
            User user = User.builder().id(userId).build();

            when(entityFacade.getStudy(studyId)).thenReturn(study);
            when(entityFacade.getUser(userId)).thenReturn(user);
            when(userStudyLikeRepository.existsUserStudyLikeByUserAndStudy(user, study)).thenReturn(true);

            // when
            userStudyLikeService.like(studyId, userId);

            // then
            verify(userStudyLikeRepository, never()).save(any(UserStudyLike.class));
        }
    }

    @Nested
    @DisplayName("좋아요 취소 기능")
    class UnlikeTest {

        @Test
        @DisplayName("좋아요 취소 성공")
        void unlike() {

            // given
            Long studyId = 1L;
            Long userId = 1L;

            Study study = Study.builder().id(studyId).build();
            User user = User.builder().id(userId).build();

            when(entityFacade.getStudy(studyId)).thenReturn(study);
            when(entityFacade.getUser(userId)).thenReturn(user);
            when(userStudyLikeRepository.existsUserStudyLikeByUserAndStudy(user, study)).thenReturn(true);

            // when
            userStudyLikeService.unlike(studyId, userId);

            // then
            verify(userStudyLikeRepository).deleteByUserAndStudy(user, study);
        }

        @Test
        @DisplayName("좋아요 취소 실패")
        void alreadyUnliked_unlike_fail() {
            // given
            Long studyId = 1L;
            Long userId = 1L;

            Study study = Study.builder().id(studyId).build();
            User user = User.builder().id(userId).build();

            when(entityFacade.getStudy(studyId)).thenReturn(study);
            when(entityFacade.getUser(userId)).thenReturn(user);
            when(userStudyLikeRepository.existsUserStudyLikeByUserAndStudy(user, study)).thenReturn(false);

            // when
            userStudyLikeService.unlike(studyId, userId);

            // then
            verify(userStudyLikeRepository, never()).deleteByUserAndStudy(user, study);
        }

    }

}
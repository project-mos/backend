package com.mos.backend.users.application;

import com.mos.backend.common.exception.MosException;
import com.mos.backend.common.infrastructure.EntityFacade;
import com.mos.backend.studies.entity.Category;
import com.mos.backend.users.application.responsedto.UserDetailRes;
import com.mos.backend.users.entity.User;
import com.mos.backend.users.entity.exception.UserErrorCode;
import com.mos.backend.users.infrastructure.respository.UserRepositoryImpl;
import com.mos.backend.users.presentation.requestdto.UserUpdateReq;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserService 테스트")
class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private EntityFacade entityFacade;

    @Mock
    private UserRepositoryImpl userRepository;

    private Long userId;
    static final String NICKNAME = "nickname";
    static final String INTRODUCTION = "introduction";
    static final String CATEGORIES = "BOOK";

    @BeforeEach
    void setUp() {
        userId = 1L;
    }

    @Nested
    @DisplayName("유저 정보 수정 성공 시나리오")
    class UserUpdateSuccess {
        @Test
        @DisplayName("유저 정보 수정 성공")
        void updateUser_Success() {
            // Given
            User user = User.builder()
                    .id(userId)
                    .nickname(NICKNAME)
                    .introduction(INTRODUCTION)
                    .categories(CATEGORIES)
                    .build();

            final String updatedNickname = "updatedNickname";
            final String updatedIntroduction = "updatedIntroduction";
            final List<Category> updatedCategories = List.of(Category.BOOK, Category.PROGRAMMING);

            UserUpdateReq validReq = new UserUpdateReq(updatedNickname, updatedIntroduction, updatedCategories);

            when(entityFacade.getUser(userId)).thenReturn(user);

            // When
            userService.update(1L, validReq);

            // Then
            String categories = updatedCategories.stream().map(Category::getDescription).collect(Collectors.joining(","));

            assertThat(user.getNickname()).isEqualTo(updatedNickname);
            assertThat(user.getIntroduction()).isEqualTo(updatedIntroduction);
            assertThat(user.getCategories()).isEqualTo(categories);
        }
    }

    @Nested
    @DisplayName("유저 정보 수정 실패 시나리오")
    class UserUpdateFail {
        @Test
        @DisplayName("존재하지 않은 userId 입력 시 예외 발생")
        void updateUser_Fail_UserIdNotFound() {
            // Given
            when(entityFacade.getUser(any(Long.class))).thenThrow(new MosException(UserErrorCode.USER_NOT_FOUND));

            // When
            MosException e = assertThrows(MosException.class, () -> userService.update(userId, any(UserUpdateReq.class)));

            // Then
            assertThat(e.getErrorCode()).isEqualTo(UserErrorCode.USER_NOT_FOUND);
        }
    }

    @Nested
    @DisplayName("유저 정보 조회 성공 시나리오")
    class UserGetSuccess {
        @Test
        @DisplayName("유저 정보 조회 성공")
        void getDetail_Success() {
            // Given
            User user = User.builder()
                    .id(userId)
                    .nickname(NICKNAME)
                    .introduction(INTRODUCTION)
                    .categories(CATEGORIES)
                    .build();

            when(entityFacade.getUser(userId)).thenReturn(user);

            // When
            UserDetailRes detailRes = userService.getDetail(userId);

            // Then
            assertThat(detailRes.getNickname()).isEqualTo(NICKNAME);
            assertThat(detailRes.getIntroduction()).isEqualTo(INTRODUCTION);
            assertThat(detailRes.getCategories()).isEqualTo(CATEGORIES);
        }
    }

    @Nested
    @DisplayName("유저 정보 조회 실패 시나리오")
    class UserGetFail {
        @Test
        @DisplayName("존재하지 않은 userId 입력 시 예외 발생")
        void getDetail_Fail_UserIdNotFound() {
            // Given
            when(entityFacade.getUser(any(Long.class))).thenThrow(new MosException(UserErrorCode.USER_NOT_FOUND));

            // When
            MosException e = assertThrows(MosException.class, () -> userService.getDetail(userId));

            // Then
            assertThat(e.getErrorCode()).isEqualTo(UserErrorCode.USER_NOT_FOUND);
        }
    }
}

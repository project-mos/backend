package com.mos.backend.notifications.application;

import com.mos.backend.common.infrastructure.EntityFacade;
import com.mos.backend.notifications.entity.UserFcmToken;
import com.mos.backend.notifications.infrastructure.userfcmtoken.UserFcmTokenRepository;
import com.mos.backend.users.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserFcmTokenServiceTest {

    @Mock
    private UserFcmTokenRepository userFcmTokenRepository;

    @Mock
    private EntityFacade entityFacade;

    @InjectMocks
    private UserFcmTokenService userFcmTokenService;

    @Captor
    private ArgumentCaptor<UserFcmToken> userFcmTokenCaptor;

    @Test
    @DisplayName("UserFcmToken을 생성한다.")
    void createTest() {
        // given
        Long userId = 1L;
        String token = "임시 토큰";
        User mockUser = Mockito.mock(User.class);
        when(mockUser.getId()).thenReturn(userId);
        when(entityFacade.getUser(userId)).thenReturn(mockUser);

        // when
        userFcmTokenService.create(userId, token);

        // then
        verify(userFcmTokenRepository).save(userFcmTokenCaptor.capture());
        UserFcmToken userFcmToken = userFcmTokenCaptor.getValue();
        assertThat(userFcmToken.getUser().getId()).isEqualTo(userId);
        assertThat(userFcmToken.getToken()).isEqualTo("임시 토큰");
        assertThat(userFcmToken.getUser()).isEqualTo(mockUser);
    }

    @Test
    @DisplayName("userId와 token을 받아 UserFcmToken을 삭제한다.")
    void deleteTest() {
        // given
        Long userId = 1L;
        String token = "임시 토큰";
        User mockUser = Mockito.mock(User.class);
        when(entityFacade.getUser(userId)).thenReturn(mockUser);

        // when
        userFcmTokenService.delete(userId, token);

        // then
        verify(userFcmTokenRepository).deleteByUserAndToken(eq(mockUser), eq(token));
    }

    @Test
    @DisplayName("userId를 받아 UserFcmToken 리스트를 조회한다.")
    void findByUserIdTest() {

        //given
        Long userId = 1L;

        UserFcmToken mockToken1 = Mockito.mock(UserFcmToken.class);
        UserFcmToken mockToken2 = Mockito.mock(UserFcmToken.class);
        List<UserFcmToken> mockTokenList = List.of(mockToken1, mockToken2);

        given(userFcmTokenRepository.findByUserId(userId)).willReturn(mockTokenList);

        // when
        List<UserFcmToken> userFcmTokenList = userFcmTokenService.findByUserId(userId);

        // then
        verify(userFcmTokenRepository).findByUserId(userId);
        assertThat(userFcmTokenList).isEqualTo(mockTokenList);
        assertThat(userFcmTokenList).hasSize(2);
    }
}
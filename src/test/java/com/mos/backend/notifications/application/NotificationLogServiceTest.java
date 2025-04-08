package com.mos.backend.notifications.application;

import com.mos.backend.common.event.EventType;
import com.mos.backend.common.infrastructure.EntityFacade;
import com.mos.backend.notifications.entity.NotificationLog;
import com.mos.backend.notifications.infrastructure.notificationlog.NotificationLogRepository;
import com.mos.backend.users.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationLogServiceTest {

    @Mock
    private NotificationLogRepository notificationLogRepository;

    @Mock
    private EntityFacade entityFacade;

    @InjectMocks
    private NotificationLogService notificationLogService;

    @Captor private ArgumentCaptor<NotificationLog> notificationLogCaptor;

    @Test
    @DisplayName("알림 로그 생성 성공")
    public void createTest() {

        //given
        Long userId = 1L;
        EventType type = EventType.STUDY_JOINED;
        String title = "test title";
        String content = "test content";
        User mockUser = Mockito.mock(User.class);
        when(entityFacade.getUser(userId)).thenReturn(mockUser);

        // when
        notificationLogService.create(userId, type, title, content);

        // then
        verify(notificationLogRepository).save(notificationLogCaptor.capture());
        NotificationLog notificationLog = notificationLogCaptor.getValue();
        assertThat(notificationLog.getRecipient()).isEqualTo(mockUser);
        assertThat(notificationLog.getTitle()).isEqualTo(title);
        assertThat(notificationLog.getContent()).isEqualTo(content);
        assertThat(notificationLog.getType()).isEqualTo(type);
    }

    @Test
    @DisplayName("알림 읽기 성공")
    public void readTest() {

        // given
        Long notificationLogId = 1L;
        NotificationLog mockNotificationLog = mock(NotificationLog.class);
        when(entityFacade.getNotificationLog(1L)).thenReturn(mockNotificationLog);

        // when
        notificationLogService.read(notificationLogId);

        // then
        verify(mockNotificationLog).read();
    }
}
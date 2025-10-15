package com.mos.backend.notifications.application;

import com.mos.backend.common.event.EventType;
import com.mos.backend.common.infrastructure.EntityFacade;
import com.mos.backend.notifications.application.dto.NotificationDetails;
import com.mos.backend.notifications.application.dto.NotificationListResponseDto;
import com.mos.backend.notifications.application.dto.NotificationResponseDto;
import com.mos.backend.notifications.application.dto.NotificationUnreadCountDto;
import com.mos.backend.notifications.application.dto.payload.DataPayload;
import com.mos.backend.notifications.entity.NotificationLog;
import com.mos.backend.notifications.entity.NotificationReadStatus;
import com.mos.backend.notifications.infrastructure.notificationlog.NotificationLogRepository;
import com.mos.backend.users.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

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

    @Captor
    private ArgumentCaptor<List<NotificationLog>> logsCaptor;

    @Test
    @DisplayName("saveAll 메서드는 여러 수신자에 대한 알림 로그를 성공적으로 생성해야 한다.")
    void saveAll_Success() {
        //given
        DataPayload mockPayload = mock(DataPayload.class);
        NotificationDetails details = NotificationDetails.builder()
                .recipientIds(List.of(1L, 2L)) // 2명의 수신자
                .eventType(EventType.STUDY_JOINED)
                .title("테스트 제목")
                .content("테스트 내용")
                .dataPayload(mockPayload)
                .build();

        when(entityFacade.getUser(1L)).thenReturn(mock(User.class));
        when(entityFacade.getUser(2L)).thenReturn(mock(User.class));

        // when
        notificationLogService.saveAll(details);

        // then
        verify(notificationLogRepository).saveAll(logsCaptor.capture());
        List<NotificationLog> capturedLogs = logsCaptor.getValue();

        assertThat(capturedLogs).hasSize(2);
        assertThat(capturedLogs.get(0).getTitle()).isEqualTo("테스트 제목");
        assertThat(capturedLogs.get(0).getType()).isEqualTo(EventType.STUDY_JOINED);
        assertThat(capturedLogs.get(0).getPayload()).isEqualTo(mockPayload);
    }

    @Test
    @DisplayName("read 메서드는 알림을 읽음 처리하고, 업데이트된 DTO를 반환해야 한다.")
    void read_Success() {
        // given
        Long notificationLogId = 1L;
        Long recipientId = 10L;

        NotificationLog mockNotificationLog = mock(NotificationLog.class);
        User mockUser = mock(User.class);

        when(entityFacade.getNotificationLog(notificationLogId)).thenReturn(mockNotificationLog);
        when(mockNotificationLog.getRecipient()).thenReturn(mockUser);
        when(mockUser.getId()).thenReturn(recipientId);

        // when: 읽음 처리 메서드 호출
        notificationLogService.read(notificationLogId);

        // then
        verify(mockNotificationLog, times(1)).read();
    }


    @Test
    @DisplayName("읽지 않은 알림 개수를 정확히 조회해야 한다.")
    void getUnreadCount_Success() {
        // given
        Long userId = 1L;
        int expectedCount = 5;
        when(notificationLogRepository.getUnreadCount(userId)).thenReturn(expectedCount);

        // when
        NotificationUnreadCountDto result = notificationLogService.getUnreadCount(userId);

        // then
        verify(notificationLogRepository).getUnreadCount(userId);
        assertThat(result).isNotNull();
        assertThat(result.getUnreadCount()).isEqualTo(expectedCount);
    }

    @Test
    @DisplayName("사용자의 알림 목록을 페이지네이션하여 정확히 조회해야 한다.")
    void getNotifications_Success() {
        // given
        Long userId = 1L;
        Pageable pageable = PageRequest.of(0, 10);
        NotificationReadStatus status = NotificationReadStatus.ALL;

        User mockUser = mock(User.class);
        when(mockUser.getId()).thenReturn(userId);

        NotificationLog log1 = mock(NotificationLog.class);
        when(log1.getId()).thenReturn(1L);
        when(log1.getRecipient()).thenReturn(mockUser); // NullPointerException 해결
        when(log1.getType()).thenReturn(EventType.STUDY_CREATED);
        when(log1.getTitle()).thenReturn("Title 1");
        when(log1.getContent()).thenReturn("Content 1");
        when(log1.isRead()).thenReturn(true);
        when(log1.getCreatedAt()).thenReturn(LocalDateTime.now());
        when(log1.getPayload()).thenReturn(mock(DataPayload.class));

        NotificationLog log2 = mock(NotificationLog.class);
        when(log2.getId()).thenReturn(2L);
        when(log2.getRecipient()).thenReturn(mockUser); // NullPointerException 해결
        when(log2.getType()).thenReturn(EventType.STUDY_CREATED);
        when(log2.getTitle()).thenReturn("Title 2");
        when(log2.getContent()).thenReturn("Content 2");
        when(log2.isRead()).thenReturn(true);
        when(log2.getCreatedAt()).thenReturn(LocalDateTime.now());
        when(log2.getPayload()).thenReturn(mock(DataPayload.class));
        List<NotificationLog> notificationLogs = List.of(log1, log2);
        Page<NotificationLog> responsePage = new PageImpl<>(notificationLogs, pageable, notificationLogs.size());

        when(notificationLogRepository.getNotifications(pageable, userId, status)).thenReturn(responsePage);

        // when
        NotificationListResponseDto result = notificationLogService.getNotifications(pageable, userId, status);

        // then
        verify(notificationLogRepository).getNotifications(pageable, userId, status);

        assertThat(result).isNotNull();
        assertThat(result.getTotalPages()).isEqualTo(1);
        assertThat(result.getNotifications()).hasSize(2);
        assertThat(result.getNotifications().get(0)).isInstanceOf(NotificationResponseDto.class);
    }
}
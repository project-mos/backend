package com.mos.backend.notifications.entity;

import com.mos.backend.common.event.EventType;
import com.mos.backend.users.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class NotificationLogTest {

    @Test
    @DisplayName("Notification 생성 초기 상태에서 read 메서드 실행 시 isRead는 항상 true를 반환한다.")
    void givenNotificationLogInitialized_WhenRead_ThenIsReadTrue() {
        // given
        User user = Mockito.mock(User.class);
        NotificationLog notificationLog = NotificationLog.create(user, EventType.STUDY_JOINED, "test title", "test content");

        // when
        notificationLog.read();

        // then
        assertThat(notificationLog.isRead()).isTrue();
    }

    @Test
    @DisplayName("isRead가 True인 상태에서 read 메서드 실행 시 isRead는 항상 true를 반환한다.")
    void givenIsReadTrue_WhenRead_ThenIsReadTrue() {
        // given
        User user = Mockito.mock(User.class);
        NotificationLog notificationLog = NotificationLog.create(user, EventType.STUDY_JOINED, "test title", "test content");
        notificationLog.read();

        // when
        notificationLog.read();

        // then
        assertThat(notificationLog.isRead()).isTrue();
    }
}
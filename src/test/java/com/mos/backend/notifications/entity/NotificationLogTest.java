package com.mos.backend.notifications.entity;

import com.mos.backend.common.event.EventType;
import com.mos.backend.notifications.application.dto.payload.DataPayload;
import com.mos.backend.users.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class NotificationLogTest {

    @Test
    @DisplayName("NotificationLog 생성 시 isRead는 기본적으로 false 상태여야 한다.")
    void notificationLog_InitialState_IsReadShouldBeFalse() {
        // given: Builder를 사용하여 NotificationLog 객체를 생성
        User mockUser = mock(User.class);
        DataPayload mockPayload = mock(DataPayload.class);

        NotificationLog notificationLog = NotificationLog.builder()
                .recipient(mockUser)
                .type(EventType.STUDY_JOINED)
                .title("테스트 제목")
                .content("테스트 내용")
                .payload(mockPayload)
                .build();

        assertThat(notificationLog.isRead()).isFalse();
    }

    @Test
    @DisplayName("Notification 생성 초기 상태에서 read 메서드 실행 시 isRead는 항상 true를 반환한다.")
    void givenNotificationLogInitialized_WhenRead_ThenIsReadTrue() {
        // given
        User mockUser = mock(User.class);
        DataPayload mockPayload = mock(DataPayload.class);
        NotificationLog notificationLog = NotificationLog.builder()
                .recipient(mockUser)
                .type(EventType.STUDY_JOINED)
                .title("테스트 제목")
                .content("테스트 내용")
                .payload(mockPayload)
                .build();

        // when
        notificationLog.read();

        // then
        assertThat(notificationLog.isRead()).isTrue();
    }

    @Test
    @DisplayName("isRead가 True인 상태에서 read 메서드 실행 시 isRead는 항상 true를 반환한다.")
    void givenIsReadTrue_WhenRead_ThenIsReadTrue() {
        // given
        User mockUser = mock(User.class);
        DataPayload mockPayload = mock(DataPayload.class);
        NotificationLog notificationLog = NotificationLog.builder()
                .recipient(mockUser)
                .type(EventType.STUDY_JOINED)
                .title("테스트 제목")
                .content("테스트 내용")
                .payload(mockPayload)
                .build();
        notificationLog.read();

        // when
        notificationLog.read();

        // then
        assertThat(notificationLog.isRead()).isTrue();
    }
}
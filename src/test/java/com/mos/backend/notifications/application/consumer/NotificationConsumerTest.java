package com.mos.backend.notifications.application.consumer;

import com.mos.backend.common.event.Event;
import com.mos.backend.common.event.EventType;
import com.mos.backend.common.event.NotificationPayload;
import com.mos.backend.notifications.application.NotificationLogService;
import com.mos.backend.notifications.application.dto.NotificationDetails;
import com.mos.backend.notifications.application.dto.payload.DataPayload;
import com.mos.backend.notifications.application.eventhandler.NotificationEventHandler;
import com.mos.backend.notifications.application.eventhandler.NotificationEventHandlerDispatcher;
import com.mos.backend.notifications.application.sending.SendingService;
import com.mos.backend.studymaterials.application.event.FileUploadedEventPayloadWithNotification;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationConsumerTest {

    @Mock
    private NotificationEventHandlerDispatcher dispatcher;
    @Mock
    private NotificationLogService notificationLogService;
    @Mock
    private SendingService sendingService;

    @Mock
    private NotificationEventHandler<NotificationPayload> mockHandler;

    @InjectMocks
    private NotificationConsumer notificationConsumer;

    private NotificationDetails mockDetails;

    @BeforeEach
    void setUp() {
        // 테스트 전반에 사용될 공통 NotificationDetails 객체 설정
        mockDetails = NotificationDetails.builder()
                .recipientIds(List.of(1L, 2L))
                .title("Test Title")
                .content("Test Content")
                .dataPayload(mock(DataPayload.class))
                .loggable(true)
                .build();
    }

    @Test
    @DisplayName("비동기 핸들러: 파일 업로드 성공 이벤트 수신 시, 알림 발송과 로그 저장을 정상적으로 호출한다.")
    void handleAsyncNotificationEvent_Success() {
        // given
        EventType eventType = EventType.FILE_UPLOADED;
        FileUploadedEventPayloadWithNotification payload = new FileUploadedEventPayloadWithNotification(1L, 10L, "test.pdf");
        Event<FileUploadedEventPayloadWithNotification> event = new Event<>(eventType, payload);

        // Mock 설정: Dispatcher가 핸들러를 반환하고, 핸들러가 Details를 반환하도록 설정
        when(dispatcher.findNotificationHandler(eventType)).thenReturn((NotificationEventHandler) mockHandler);
        when(mockHandler.prepareDetails(eventType, payload)).thenReturn(mockDetails);

        // when
        notificationConsumer.handleAsyncNotificationEvent(event);

        // then
        verify(sendingService, times(1)).sendMulticastMessage(
                mockDetails.getRecipientIds(),
                mockDetails.getTitle(),
                mockDetails.getContent(),
                mockDetails.getDataPayload()
        );
        verify(notificationLogService, times(1)).saveAll(mockDetails);
    }

    @Test
    @DisplayName("트랜잭션 핸들러: 스터디 멤버 생성 이벤트 수신 시, 알림 발송과 로그 저장을 정상적으로 호출한다.")
    void handleTransactionalNotificationEvent_Success() {
        // given
        // 파일 이벤트가 아닌 다른 이벤트 타입 사용 (예: STUDY_MEMBER_CREATED)
        EventType eventType = EventType.STUDY_MEMBER_CREATED;
        NotificationPayload payload = mock(NotificationPayload.class);
        Event<NotificationPayload> event = new Event<>(eventType, payload);

        when(dispatcher.findNotificationHandler(eventType)).thenReturn(mockHandler);
        when(mockHandler.prepareDetails(eventType, payload)).thenReturn(mockDetails);

        // when
        notificationConsumer.handleNotificationEvent(event);

        // then
        verify(sendingService, times(1)).sendMulticastMessage(
                mockDetails.getRecipientIds(),
                mockDetails.getTitle(),
                mockDetails.getContent(),
                mockDetails.getDataPayload()
        );
        verify(notificationLogService, times(1)).saveAll(mockDetails);
    }

    @Test
    @DisplayName("비동기 핸들러: 파일 관련 이벤트가 아니면 무시해야 한다.")
    void handleAsyncNotificationEvent_ShouldIgnoreNonFileEvents() {
        // given
        EventType eventType = EventType.STUDY_MEMBER_CREATED; // 파일 관련 이벤트가 아님
        Event<NotificationPayload> event = new Event<>(eventType, mock(NotificationPayload.class));

        // when
        notificationConsumer.handleAsyncNotificationEvent(event);

        // then
        // 어떤 서비스도 호출되지 않아야 함
        verifyNoInteractions(dispatcher, sendingService, notificationLogService);
    }

    @Test
    @DisplayName("트랜잭션 핸들러: 파일 관련 이벤트는 무시해야 한다.")
    void handleTransactionalNotificationEvent_ShouldIgnoreFileEvents() {
        // given
        EventType eventType = EventType.FILE_UPLOADED; // 파일 관련 이벤트
        Event<NotificationPayload> event = new Event<>(eventType, mock(NotificationPayload.class));

        // when
        notificationConsumer.handleNotificationEvent(event);

        // then
        // 어떤 서비스도 호출되지 않아야 함
        verifyNoInteractions(dispatcher, sendingService, notificationLogService);
    }


    @Test
    @DisplayName("수신자 목록이 비어있으면, 알림 발송과 로그 저장을 호출하지 않아야 한다.")
    void processNotification_WhenNoRecipients_ShouldDoNothing() {
        // given
        EventType eventType = EventType.FILE_UPLOADED;
        Event<NotificationPayload> event = new Event<>(eventType, mock(NotificationPayload.class));

        // 수신자가 없는 Details 객체
        NotificationDetails detailsWithNoRecipients = NotificationDetails.builder()
                .recipientIds(Collections.emptyList())
                .build();

        when(dispatcher.findNotificationHandler(eventType)).thenReturn(mockHandler);
        when(mockHandler.prepareDetails(any(), any())).thenReturn(detailsWithNoRecipients);

        // when
        // 비동기 핸들러를 통해 테스트 (어떤 핸들러든 결과는 동일)
        notificationConsumer.handleAsyncNotificationEvent(event);

        // then
        verify(sendingService, never()).sendMulticastMessage(any(), any(), any(), any());
        verify(notificationLogService, never()).saveAll(any());
    }

    @Test
    @DisplayName("Dispatcher가 핸들러를 찾지 못하면 예외가 발생하고, 다른 서비스는 호출되지 않아야 한다.")
    void processNotification_WhenNoHandlerFound_ShouldThrowException() {
        // given
        EventType eventType = EventType.FILE_UPLOADED;
        Event<NotificationPayload> event = new Event<>(eventType, mock(NotificationPayload.class));

        // Dispatcher가 예외를 던지도록 설정
        when(dispatcher.findNotificationHandler(eventType)).thenThrow(new IllegalArgumentException("No handler found"));

        // when & then
        assertThrows(IllegalArgumentException.class, () -> {
            notificationConsumer.handleAsyncNotificationEvent(event);
        });

        verify(sendingService, never()).sendMulticastMessage(any(), any(), any(), any());
        verify(notificationLogService, never()).saveAll(any());
    }
}
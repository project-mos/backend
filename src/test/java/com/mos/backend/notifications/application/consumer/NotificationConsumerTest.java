package com.mos.backend.notifications.application.consumer;

import com.mos.backend.common.event.Event;
import com.mos.backend.common.event.EventType;
import com.mos.backend.common.event.NotificationPayload;
import com.mos.backend.notifications.application.NotificationLogService;
import com.mos.backend.notifications.application.dto.NotificationDetails;
import com.mos.backend.notifications.application.dto.payload.DataPayload;
import com.mos.backend.notifications.application.dto.payload.StudyFileUploadPayload;
import com.mos.backend.notifications.application.eventhandler.NotificationEventHandler;
import com.mos.backend.notifications.application.eventhandler.NotificationEventHandlerDispatcher;
import com.mos.backend.notifications.application.sending.SendingService;
import com.mos.backend.studymaterials.application.event.FileUploadedEventPayloadWithNotification;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertThrows;
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
    private NotificationEventHandler<?> mockHandler;

    @InjectMocks
    private NotificationConsumer notificationConsumer;


    @Test
    @DisplayName("이벤트 수신 시, 핸들러를 통해 Details를 생성하고 각 서비스를 한 번씩만 호출해야 한다.")
    void handleNotificationEvent_Success() {
        // given
        EventType eventType = EventType.FILE_UPLOADED;
        FileUploadedEventPayloadWithNotification payload = new FileUploadedEventPayloadWithNotification(1L, 10L, "test.pdf");
        Event<FileUploadedEventPayloadWithNotification> event = Event.create(eventType, payload);

        when(dispatcher.findNotificationHandler(eventType)).thenReturn(mockHandler);

        DataPayload dataPayload = StudyFileUploadPayload.success(10L);
        NotificationDetails details = NotificationDetails.builder()
                .recipientIds(List.of(1L, 2L, 3L))
                .eventType(eventType)
                .title("제목")
                .content("내용")
                .dataPayload(dataPayload)
                .build();

        when(((NotificationEventHandler<FileUploadedEventPayloadWithNotification>) mockHandler).prepareDetails(eventType, payload))
                .thenReturn(details);

        // when
        notificationConsumer.handleNotificationEvent(event);

        // then
        verify(sendingService, times(1)).sendMulticastMessage(
                details.getRecipientIds(),
                details.getTitle(),
                details.getContent(),
                details.getDataPayload()
        );
        verify(notificationLogService, times(1)).saveAll(details);
    }

    @Test
    @DisplayName("핸들러가 반환한 수신자 목록이 비어있으면, 어떤 서비스도 호출하지 않아야 한다.")
    void handleNotificationEvent_WhenNoRecipients_ShouldDoNothing() {
        // given
        EventType eventType = EventType.FILE_UPLOADED;
        Event<NotificationPayload> event = Event.create(eventType, mock(NotificationPayload.class));
        when(dispatcher.findNotificationHandler(eventType)).thenReturn(mockHandler);

        // 수신자 목록이 비어있는 NotificationDetails를 생성
        NotificationDetails detailsWithNoRecipients = NotificationDetails.builder()
                .recipientIds(Collections.emptyList())
                .build();
        when(mockHandler.prepareDetails(any(), any())).thenReturn(detailsWithNoRecipients);

        // when
        notificationConsumer.handleNotificationEvent(event);

        // then
        // 어떤 서비스도 호출되지 않았는지 검증
        verify(sendingService, never()).sendMulticastMessage(any(), any(), any(), any());
        verify(notificationLogService, never()).saveAll(any());
    }

    @Test
    @DisplayName("Dispatcher가 핸들러를 찾지 못하고 예외를 던져도, Consumer는 중단되지 않고 서비스들을 호출하지 않아야 한다.")
    void handleNotificationEvent_WhenNoHandlerFound_ShouldNotCallServices() {
        // given
        EventType eventType = EventType.FILE_UPLOADED;
        Event<NotificationPayload> event = Event.create(eventType, mock(NotificationPayload.class));

        // Dispatcher가 예외를 던지도록 설정
        when(dispatcher.findNotificationHandler(eventType)).thenThrow(new IllegalArgumentException("Test Exception"));

        assertThrows(IllegalArgumentException.class, () -> {
            notificationConsumer.handleNotificationEvent(event);
        });

        verify(sendingService, never()).sendMulticastMessage(any(), any(), any(), any());
        verify(notificationLogService, never()).saveAll(any());
    }
}
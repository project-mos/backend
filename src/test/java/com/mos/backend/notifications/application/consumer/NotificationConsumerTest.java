package com.mos.backend.notifications.application.consumer;

import com.mos.backend.common.event.Event;
import com.mos.backend.common.event.EventType;
import com.mos.backend.common.event.NotificationPayload;
import com.mos.backend.notifications.application.NotificationLogService;
import com.mos.backend.notifications.application.dto.DataPayloadDto;
import com.mos.backend.notifications.application.dto.NotificationDetails;
import com.mos.backend.notifications.application.eventhandler.NotificationEventHandler;
import com.mos.backend.notifications.application.eventhandler.NotificationEventHandlerDispatcher;
import com.mos.backend.notifications.application.sending.SendingService;
import com.mos.backend.studymaterials.application.event.FileUploadedEventPayloadWithNotification;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NotificationConsumerTest {

    @Mock
    private NotificationEventHandlerDispatcher dispatcher;
    @Mock
    private NotificationLogService notificationLogService;
    @Mock
    private SendingService sendingService;
    @Mock
    private NotificationEventHandler<FileUploadedEventPayloadWithNotification> mockHandler;

    @InjectMocks
    private NotificationConsumer notificationConsumer;

    private EventType type = EventType.FILE_UPLOADED;
    private Long userId = 1L;
    private Long studyId = 1L;
    private String originalFilename = "testFileName";
    private String studyName = "testStudyName";


    @Test
    @DisplayName("NotificationDetails 리스트의 크기가 1개 이상일 때 리스트의 크기 만큼 service를 호출한다.")
    void whenNotificationDetailsSizeOverThan1_ThenRequestServiceMethodSizeEqualsListSize() {

        // given
        FileUploadedEventPayloadWithNotification fileUploadedEventPayloadWithNotification = new FileUploadedEventPayloadWithNotification(userId, studyId, originalFilename);
        Event<FileUploadedEventPayloadWithNotification> event = Event.create(type, fileUploadedEventPayloadWithNotification);

        when(dispatcher.findNotificationHandler(event.getEventType())).thenReturn(mockHandler);

        DataPayloadDto dataPayloadDto = DataPayloadDto.forFileUpload(type, studyId, studyName, originalFilename);
        NotificationDetails notificationDetails1 = NotificationDetails.forFileUploaded(userId, "title", "content", dataPayloadDto);
        NotificationDetails notificationDetails2 = NotificationDetails.forFileUploaded(userId, "title", "content", dataPayloadDto);

        List<NotificationDetails> notificationDetailsList = List.of(notificationDetails1, notificationDetails2);

        when(mockHandler.prepareDetails(event.getEventType(), event.getPayload())).thenReturn(notificationDetailsList);

        // when
        notificationConsumer.handleNotificationEvent(event);

        // then
        verify(notificationLogService, times(notificationDetailsList.size())).create(anyLong(), any(EventType.class), anyString(), anyString());
        verify(sendingService, times(notificationDetailsList.size())).sendMessage(anyLong(), anyString(), anyString(), any(DataPayloadDto.class));
    }

    @Test
    @DisplayName("Dispatcher가 핸들러를 찾지 못하면 Log/Send 서비스는 호출되지 않는다")
    void handleNotificationEvent_NoHandlerFound() {
        // given
        Event<NotificationPayload> event = Event.create(type, mock(NotificationPayload.class));
        when(dispatcher.findNotificationHandler(event.getEventType())).thenThrow(new IllegalArgumentException("cannot find proper handler"));

        // when
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> notificationConsumer.handleNotificationEvent(event));

        // then
        assertThat(exception.getMessage()).isEqualTo("cannot find proper handler");
        verify(notificationLogService, never()).create(anyLong(), any(EventType.class), anyString(), anyString());
        verify(sendingService, never()).sendMessage(anyLong(), anyString(), anyString(), any(DataPayloadDto.class));
    }
}
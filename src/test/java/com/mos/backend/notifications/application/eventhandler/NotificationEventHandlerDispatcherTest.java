package com.mos.backend.notifications.application.eventhandler;

import com.mos.backend.common.event.EventType;
import com.mos.backend.notifications.application.eventhandler.impl.StudyFileUploadFailedEventHandler;
import com.mos.backend.notifications.application.eventhandler.impl.StudyFileUploadedEventHandler;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NotificationEventHandlerDispatcherTest {

    @Mock
    private StudyFileUploadedEventHandler studyFileUploadedEventHandler;

    @Mock
    private StudyFileUploadFailedEventHandler studyFileUploadFailedEventHandler;

    private NotificationEventHandlerDispatcher dispatcher;

    @Test
    @DisplayName("지원하는 Type의 handler가 존재하면 handler를 반환한다.")
    void givenSupportedEventType_WhenFindNotificationHandlerMethod_ThenReturnHandler() {

        // given
        when(studyFileUploadedEventHandler.support()).thenReturn(EventType.FILE_UPLOADED);
        when(studyFileUploadFailedEventHandler.support()).thenReturn(EventType.FILE_UPLOAD_FAILED);
        NotificationEventHandlerDispatcher dispatcher = new NotificationEventHandlerDispatcher(List.of(studyFileUploadedEventHandler, studyFileUploadFailedEventHandler));

        // when
        NotificationEventHandler notificationHandler = dispatcher.findNotificationHandler(EventType.FILE_UPLOADED);

        // then
        assertThat(notificationHandler).isSameAs(studyFileUploadedEventHandler);
    }

    @Test
    @DisplayName("지원하는 Type의 handler가 존재하지 않으면 에러를 발생한다.")
    void givenNotSupportedEventType_WhenFindNotificationHandlerMethod_ThenThrowException() {
        // given
        when(studyFileUploadedEventHandler.support()).thenReturn(EventType.FILE_UPLOADED);
        when(studyFileUploadFailedEventHandler.support()).thenReturn(EventType.FILE_UPLOAD_FAILED);
        NotificationEventHandlerDispatcher dispatcher = new NotificationEventHandlerDispatcher(List.of(studyFileUploadedEventHandler, studyFileUploadFailedEventHandler));

        // when
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> dispatcher.findNotificationHandler(EventType.STUDY_CREATED));

        // then
        assertThat(exception.getMessage()).isEqualTo("cannot find proper handler");
    }
}
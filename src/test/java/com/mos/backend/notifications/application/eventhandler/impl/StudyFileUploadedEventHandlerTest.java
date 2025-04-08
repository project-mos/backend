package com.mos.backend.notifications.application.eventhandler.impl;

import com.mos.backend.common.event.EventType;
import com.mos.backend.common.infrastructure.EntityFacade;
import com.mos.backend.notifications.application.dto.DataPayloadDto;
import com.mos.backend.notifications.application.dto.NotificationDetails;
import com.mos.backend.studies.entity.Study;
import com.mos.backend.studymaterials.application.event.FileUploadedEventPayloadWithNotification;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.MessageSource;

import java.util.List;
import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StudyFileUploadedEventHandlerTest {

    private static final String MESSAGE_TITLE_CODE = "notification.file-uploaded.title";
    private static final String MESSAGE_CONTENT_CODE = "notification.file-uploaded.content";

    @Mock
    private EntityFacade entityFacade;

    @Mock
    private MessageSource ms;

    @InjectMocks
    private StudyFileUploadedEventHandler studyFileUploadedEventHandler;

    @Test
    @DisplayName("support 메서드 호출 시 EventType.FILE_UPLOADED를 반환한다.")
    void whenSupportMethod_ThenReturnEventTypeFILE_UPLOADED() {
        // when - then
        assertThat(studyFileUploadedEventHandler.support()).isEqualTo(EventType.FILE_UPLOADED);
    }

    @Test
    @DisplayName("prepareDetails 메서드 호출 시 NotificationDetailsList를 반환한다.")
    void whenPrepareDetails_ThenReturnListOfNotificationDetails() {

        // given
        EventType type = EventType.FILE_UPLOADED;
        Long uploaderId = 1L;
        Long studyId = 1L;
        String originalFilename = "testFile";
        FileUploadedEventPayloadWithNotification payload = new FileUploadedEventPayloadWithNotification(uploaderId, studyId, originalFilename);

        Study mockStudy = mock(Study.class);
        String studyTitle = "testStudyTitle";
        when(mockStudy.getTitle()).thenReturn(studyTitle);
        when(entityFacade.getStudy(studyId)).thenReturn(mockStudy);


        String messageTitle = "testMessageTitle";
        String messageContent = "testMessageContent";
        when(ms.getMessage(MESSAGE_TITLE_CODE, null, Locale.getDefault())).thenReturn("testMessageTitle");
        when(ms.getMessage(MESSAGE_CONTENT_CODE, new String[]{payload.getOriginalFilename()}, Locale.getDefault())).thenReturn("testMessageContent");

        // when
        List<NotificationDetails> notificationDetails = studyFileUploadedEventHandler.prepareDetails(type, payload);

        // then
        assertThat(notificationDetails).hasSize(1);
        assertThat(notificationDetails.getFirst().getTitle()).isEqualTo(messageTitle);
        assertThat(notificationDetails.getFirst().getContent()).isEqualTo(messageContent);
        assertThat(notificationDetails.getFirst().getRecipientId()).isEqualTo(uploaderId.toString());
        DataPayloadDto dataPayloadDto = notificationDetails.getFirst().getDataPayloadDto();
        assertThat(dataPayloadDto.getType()).isEqualTo(EventType.FILE_UPLOADED.toString());
        assertThat(dataPayloadDto.getStudyId()).isEqualTo(studyId.toString());
        assertThat(dataPayloadDto.getFileName()).isEqualTo(originalFilename);
        assertThat(dataPayloadDto.getStudyName()).isEqualTo(studyTitle);
    }
}
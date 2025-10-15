package com.mos.backend.notifications.application.eventhandler.impl;

import com.mos.backend.common.event.EventType;
import com.mos.backend.common.infrastructure.EntityFacade;
import com.mos.backend.notifications.application.dto.NotificationDetails;
import com.mos.backend.notifications.application.dto.payload.DataPayload;
import com.mos.backend.notifications.application.dto.payload.StudyFileUploadPayload;
import com.mos.backend.studies.entity.Study;
import com.mos.backend.studymaterials.application.event.FileUploadFailedEventPayloadWithNotification;
import com.mos.backend.studymaterials.entity.FileUploadStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.MessageSource;

import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StudyFileUploadFailedEventHandlerTest {

    private static final String MESSAGE_TITLE_CODE = "notification.file-upload-failure.title";
    private static final String MESSAGE_CONTENT_CODE = "notification.file-upload-failure.content";

    @Mock
    private EntityFacade entityFacade;

    @Mock
    private MessageSource messageSource;

    @InjectMocks
    private StudyFileUploadFailedEventHandler studyFileUploadFailedEventHandler;

    @Test
    @DisplayName("support 메서드는 EventType.FILE_UPLOAD_FAILED를 반환해야 한다.")
    void support_ShouldReturn_EventTypeFileUploadFailed() {
        // when & then
        assertThat(studyFileUploadFailedEventHandler.support()).isEqualTo(EventType.FILE_UPLOAD_FAILED);
    }

    @Test
    @DisplayName("prepareDetails 메서드는 올바른 '실패' 상태의 NotificationDetails 객체를 생성하여 반환해야 한다.")
    void prepareDetails_ShouldReturn_CorrectFailureNotificationDetails() {

        // given: 테스트에 필요한 모든 데이터를 설정
        EventType eventType = EventType.FILE_UPLOAD_FAILED;
        Long uploaderId = 1L;
        Long studyId = 10L;
        String fileName = "failedFile.zip";
        String filePath = "path/to/file";
        var eventPayload = new FileUploadFailedEventPayloadWithNotification(uploaderId, studyId, filePath, fileName);

        Study mockStudy = mock(Study.class);
        String studyTitle = "리액트 스터디";
        when(mockStudy.getTitle()).thenReturn(studyTitle);
        when(entityFacade.getStudy(studyId)).thenReturn(mockStudy);

        String expectedTitle = "파일 업로드 실패";
        String expectedContent = "failedFile.zip 파일 업로드를 실패했습니다.";
        when(messageSource.getMessage(MESSAGE_TITLE_CODE, null, Locale.getDefault())).thenReturn(expectedTitle);
        when(messageSource.getMessage(MESSAGE_CONTENT_CODE, new Object[]{fileName}, Locale.getDefault())).thenReturn(expectedContent);

        // when
        NotificationDetails details = studyFileUploadFailedEventHandler.prepareDetails(eventType, eventPayload);

        // then
        assertThat(details).isNotNull();
        assertThat(details.getRecipientIds()).hasSize(1).containsExactly(uploaderId);
        assertThat(details.getEventType()).isEqualTo(eventType);
        assertThat(details.getTitle()).isEqualTo(expectedTitle);
        assertThat(details.getContent()).isEqualTo(expectedContent);

        DataPayload payload = details.getDataPayload();
        assertThat(payload).isInstanceOf(StudyFileUploadPayload.class);

        StudyFileUploadPayload fileUploadPayload = (StudyFileUploadPayload) payload;
        assertThat(fileUploadPayload.getStudyId()).isEqualTo(studyId);
        assertThat(fileUploadPayload.getStatus()).isEqualTo(FileUploadStatus.FAILURE);
    }
}
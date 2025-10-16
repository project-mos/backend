package com.mos.backend.notifications.application.eventhandler.impl;

import com.mos.backend.common.event.EventType;
import com.mos.backend.common.infrastructure.EntityFacade;
import com.mos.backend.notifications.application.dto.NotificationDetails;
import com.mos.backend.notifications.application.dto.payload.DataPayload;
import com.mos.backend.notifications.application.dto.payload.StudyFileUploadPayload;
import com.mos.backend.studies.entity.Study;
import com.mos.backend.studymaterials.application.event.FileUploadedEventPayloadWithNotification;
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
    @DisplayName("prepareDetails 메서드 호출 시 NotificationDetails를 반환한다.")
    void whenPrepareDetails_ThenReturnListOfNotificationDetails() {

        // given: 테스트에 필요한 모든 데이터를 설정
        EventType eventType = EventType.FILE_UPLOADED;
        Long uploaderId = 1L;
        Long studyId = 10L;
        String fileName = "testFile.pdf";
        FileUploadedEventPayloadWithNotification eventPayload = new FileUploadedEventPayloadWithNotification(uploaderId, studyId, fileName);

        Study mockStudy = mock(Study.class);
        String studyTitle = "스프링 스터디";
        when(mockStudy.getTitle()).thenReturn(studyTitle);
        when(entityFacade.getStudy(studyId)).thenReturn(mockStudy);

        String expectedTitle = "파일 업로드 완료";
        String expectedContent = studyTitle + "에 testFile.pdf 파일이 업로드되었습니다.";
        when(ms.getMessage(MESSAGE_TITLE_CODE, null, Locale.getDefault())).thenReturn(expectedTitle);
        when(ms.getMessage(MESSAGE_CONTENT_CODE, new String[]{studyTitle, fileName}, Locale.getDefault())).thenReturn(expectedContent);

        // when: 테스트 대상 메서드 호출
        NotificationDetails details = studyFileUploadedEventHandler.prepareDetails(eventType, eventPayload);

        // then: 반환된 객체의 모든 필드를 상세하게 검증
        assertThat(details).isNotNull();
        assertThat(details.getRecipientIds()).hasSize(1).containsExactly(uploaderId);
        assertThat(details.getEventType()).isEqualTo(eventType);
        assertThat(details.getTitle()).isEqualTo(expectedTitle);
        assertThat(details.getContent()).isEqualTo(expectedContent);

        // DataPayload가 올바른 타입과 값을 가졌는지 검증
        DataPayload payload = details.getDataPayload();
        assertThat(payload).isInstanceOf(StudyFileUploadPayload.class); // 정확한 자식 타입인지 확인

        // 자식 타입으로 캐스팅하여 내부 필드 검증
        StudyFileUploadPayload fileUploadPayload = (StudyFileUploadPayload) payload;
        assertThat(fileUploadPayload.getStudyId()).isEqualTo(studyId);
        assertThat(fileUploadPayload.getStatus()).isEqualTo(FileUploadStatus.SUCCESS); // '성공' 상태인지 확인
    }
}
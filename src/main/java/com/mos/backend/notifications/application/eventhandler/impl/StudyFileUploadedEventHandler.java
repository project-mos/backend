package com.mos.backend.notifications.application.eventhandler.impl;

import com.mos.backend.common.event.EventType;
import com.mos.backend.common.infrastructure.EntityFacade;
import com.mos.backend.notifications.application.dto.NotificationDetails;
import com.mos.backend.notifications.application.dto.payload.StudyFileUploadPayload;
import com.mos.backend.notifications.application.eventhandler.NotificationEventHandler;
import com.mos.backend.studies.entity.Study;
import com.mos.backend.studymaterials.application.event.FileUploadedEventPayloadWithNotification;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Locale;

@Component
@RequiredArgsConstructor
public class StudyFileUploadedEventHandler implements NotificationEventHandler<FileUploadedEventPayloadWithNotification> {

    private static final String MESSAGE_TITLE_CODE = "notification.file-uploaded.title";
    private static final String MESSAGE_CONTENT_CODE = "notification.file-uploaded.content";

    private final EntityFacade entityFacade;
    private final MessageSource ms;

    @Override
    public NotificationDetails prepareDetails(EventType type, FileUploadedEventPayloadWithNotification payload) {
        Study study = entityFacade.getStudy(payload.getStudyId());

        // 받을 사람 목록 생성(업로드한 유저)
        List<Long> recipientIdList = getRecipientIdList(payload);

        // 알림 제목, 내용 생성
        String title = ms.getMessage(MESSAGE_TITLE_CODE, null, Locale.getDefault());
        String content = ms.getMessage(MESSAGE_CONTENT_CODE, new String[]{study.getTitle(), payload.getOriginalFilename()}, Locale.getDefault());

        StudyFileUploadPayload dataPayload = StudyFileUploadPayload.success(
                payload.getStudyId()
        );

        return NotificationDetails.builder()
                .recipientIds(recipientIdList)
                .title(title)
                .content(content)
                .eventType(type)
                .dataPayload(dataPayload)
                .build();
    }

    @Override
    public EventType support() {
        return EventType.FILE_UPLOADED;
    }

    private List<Long> getRecipientIdList(FileUploadedEventPayloadWithNotification payload) {
        return List.of(payload.getUploaderId());
    }

}

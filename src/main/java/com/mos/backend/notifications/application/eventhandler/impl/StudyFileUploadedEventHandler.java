package com.mos.backend.notifications.application.eventhandler.impl;

import com.mos.backend.common.event.EventType;
import com.mos.backend.common.infrastructure.EntityFacade;
import com.mos.backend.notifications.application.eventhandler.NotificationEventHandler;
import com.mos.backend.notifications.application.dto.DataPayloadDto;
import com.mos.backend.notifications.application.dto.NotificationDetails;
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
    public List<NotificationDetails> prepareDetails(EventType type, FileUploadedEventPayloadWithNotification payload) {
        List<Long> recipientIdList = getRecipientIdList(payload);
        Study study = entityFacade.getStudy(payload.getStudyId());
        DataPayloadDto dataPayloadDto = DataPayloadDto.forFileUpload(type, payload.getStudyId(), study.getTitle(), payload.getOriginalFilename());
        return recipientIdList.stream().map(id -> {
            String title = ms.getMessage(MESSAGE_TITLE_CODE, null, Locale.getDefault());
            String content = ms.getMessage(MESSAGE_CONTENT_CODE, new String[]{payload.getOriginalFilename()}, Locale.getDefault());
            return NotificationDetails.forFileUploaded(id, title, content, dataPayloadDto);
        }).toList();
    }

    @Override
    public EventType support() {
        return EventType.FILE_UPLOADED;
    }

    private List<Long> getRecipientIdList(FileUploadedEventPayloadWithNotification payload) {
        return List.of(payload.getUploaderId());
    }

}

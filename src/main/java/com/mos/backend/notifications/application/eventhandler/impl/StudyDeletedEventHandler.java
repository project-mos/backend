package com.mos.backend.notifications.application.eventhandler.impl;

import com.mos.backend.common.event.EventType;
import com.mos.backend.common.infrastructure.EntityFacade;
import com.mos.backend.notifications.application.dto.NotificationDetails;
import com.mos.backend.notifications.application.dto.payload.StudyDeletedPayload;
import com.mos.backend.notifications.application.eventhandler.NotificationEventHandler;
import com.mos.backend.studies.application.event.StudyDeletedEventPayloadWithNotification;
import com.mos.backend.studies.entity.Study;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Component
@RequiredArgsConstructor
public class StudyDeletedEventHandler implements NotificationEventHandler<StudyDeletedEventPayloadWithNotification> {

    private static final String MESSAGE_TITLE_CODE = "notification.study.deleted.title";
    private static final String MESSAGE_CONTENT_CODE = "notification.study.deleted.content";

    private final EntityFacade entityFacade;
    private final MessageSource ms;

    @Override
    public NotificationDetails prepareDetails(EventType type, StudyDeletedEventPayloadWithNotification payload) {
        Study study = entityFacade.getStudy(payload.getStudyId());

        String title = ms.getMessage(MESSAGE_TITLE_CODE, null, Locale.getDefault());
        String content = ms.getMessage(MESSAGE_CONTENT_CODE, new String[]{study.getTitle()}, Locale.getDefault());

        StudyDeletedPayload dataPayload = new StudyDeletedPayload();

        return NotificationDetails.builder()
                .recipientIds(payload.getRecipientIds())
                .eventType(type)
                .title(title)
                .content(content)
                .dataPayload(dataPayload)
                .build();
    }

    @Override
    public EventType support() {
        return EventType.STUDY_DELETED;
    }
}

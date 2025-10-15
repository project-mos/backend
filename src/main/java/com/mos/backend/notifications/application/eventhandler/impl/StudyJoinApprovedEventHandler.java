package com.mos.backend.notifications.application.eventhandler.impl;

import com.mos.backend.common.event.EventType;
import com.mos.backend.common.infrastructure.EntityFacade;
import com.mos.backend.notifications.application.dto.NotificationDetails;
import com.mos.backend.notifications.application.dto.payload.StudyJoinResultPayload;
import com.mos.backend.notifications.application.eventhandler.NotificationEventHandler;
import com.mos.backend.studies.entity.Study;
import com.mos.backend.studyjoins.application.event.StudyJoinResultEventPayloadWithNotification;
import com.mos.backend.users.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Locale;

@Component
@RequiredArgsConstructor
public class StudyJoinApprovedEventHandler implements NotificationEventHandler<StudyJoinResultEventPayloadWithNotification> {

    private static final String JOIN_APPROVED_MESSAGE_TITLE_CODE = "notification.study.join-approved.title";
    private static final String JOIN_APPROVED_MESSAGE_CONTENT_CODE = "notification.study.join-approved.content";

    private final EntityFacade entityFacade;
    private final MessageSource ms;

    @Override
    public NotificationDetails prepareDetails(EventType type, StudyJoinResultEventPayloadWithNotification payload) {
        User applicant = entityFacade.getUser(payload.getJoinUserId());
        Study study = entityFacade.getStudy(payload.getStudyId());

        List<Long> recipientIds = List.of(applicant.getId());

        String title = ms.getMessage(JOIN_APPROVED_MESSAGE_TITLE_CODE, null, Locale.getDefault());
        String content = ms.getMessage(JOIN_APPROVED_MESSAGE_CONTENT_CODE, new String[]{study.getTitle()}, Locale.getDefault());

        StudyJoinResultPayload dataPayload = StudyJoinResultPayload.approved(study.getId(), applicant.getId());

        return NotificationDetails.builder()
                .recipientIds(recipientIds)
                .eventType(type)
                .title(title)
                .content(content)
                .dataPayload(dataPayload)
                .build();
    }

    @Override
    public EventType support() {
        return EventType.STUDY_JOIN_APPROVED;
    }
}

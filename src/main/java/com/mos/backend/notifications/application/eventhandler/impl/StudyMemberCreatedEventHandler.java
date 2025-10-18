package com.mos.backend.notifications.application.eventhandler.impl;

import com.mos.backend.common.event.EventType;
import com.mos.backend.common.infrastructure.EntityFacade;
import com.mos.backend.notifications.application.dto.NotificationDetails;
import com.mos.backend.notifications.application.dto.payload.NewStudyMemberPayload;
import com.mos.backend.notifications.application.eventhandler.NotificationEventHandler;
import com.mos.backend.studies.entity.Study;
import com.mos.backend.studymembers.application.StudyMemberService;
import com.mos.backend.studymembers.application.event.StudyMemberCreatedEventPayloadWithNotification;
import com.mos.backend.studymembers.entity.StudyMember;
import com.mos.backend.users.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Locale;

@Component
@RequiredArgsConstructor
public class StudyMemberCreatedEventHandler implements NotificationEventHandler<StudyMemberCreatedEventPayloadWithNotification> {

    private static final String NEW_MEMBER_CREATED_MESSAGE_TITLE_CODE = "notification.study.new-member.title";
    private static final String NEW_MEMBER_CREATED_MESSAGE_CONTENT_CODE = "notification.study.new-member.content";

    private final EntityFacade entityFacade;
    private final MessageSource ms;
    private final StudyMemberService studyMemberService;

    @Override
    public NotificationDetails prepareDetails(EventType type, StudyMemberCreatedEventPayloadWithNotification payload) {
        User newMember = entityFacade.getUser(payload.getNewMemberUserId());
        Study study = entityFacade.getStudy(payload.getStudyId());

        List<StudyMember> allStudyMember = studyMemberService.findAllByAndStudy(study);

        List<Long> recipientIds = allStudyMember.stream()
                .filter(member -> !member.getUser().getId().equals(newMember.getId()))
                .map(studyMember -> studyMember.getUser().getId())
                .toList();

        String title = ms.getMessage(NEW_MEMBER_CREATED_MESSAGE_TITLE_CODE, null, Locale.getDefault());
        String content = ms.getMessage(NEW_MEMBER_CREATED_MESSAGE_CONTENT_CODE, new String[]{study.getTitle(), newMember.getNickname()}, Locale.getDefault());

        NewStudyMemberPayload dataPayload = new NewStudyMemberPayload(study.getId());
        return NotificationDetails.builder()
                .recipientIds(recipientIds)
                .eventType(EventType.STUDY_MEMBER_CREATED)
                .title(title)
                .content(content)
                .dataPayload(dataPayload)
                .build();
    }

    @Override
    public EventType support() {
        return EventType.STUDY_MEMBER_CREATED;
    }
}

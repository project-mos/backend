package com.mos.backend.notifications.application.eventhandler.impl;

import com.mos.backend.common.event.EventType;
import com.mos.backend.common.infrastructure.EntityFacade;
import com.mos.backend.notifications.application.dto.NotificationDetails;
import com.mos.backend.notifications.application.dto.payload.StudyJoinedPayload;
import com.mos.backend.notifications.application.eventhandler.NotificationEventHandler;
import com.mos.backend.studies.entity.Study;
import com.mos.backend.studyjoins.application.event.StudyJoinEventPayloadWithNotification;
import com.mos.backend.studymembers.application.StudyMemberService;
import com.mos.backend.studymembers.entity.StudyMember;
import com.mos.backend.users.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Locale;

/**
 * 기존 스터디원이 받는 알림 처리
 * 스터디 지원자 발생 시 관련 푸시 알림 처리(지원, 취소)
 */
@Component
@RequiredArgsConstructor
public class StudyJoinRequestedEventHandler implements NotificationEventHandler<StudyJoinEventPayloadWithNotification> {

    private static final String JOIN_REQUESTED_MESSAGE_TITLE_CODE = "notification.study.join-requested.title";
    private static final String JOIN_REQUESTED_MESSAGE_CONTENT_CODE = "notification.study.join-requested.content";

    private final StudyMemberService studyMemberService;
    private final EntityFacade entityFacade;
    private final MessageSource ms;

    @Override
    public NotificationDetails prepareDetails(EventType type, StudyJoinEventPayloadWithNotification payload) {
        Study study = entityFacade.getStudy(payload.getStudyId());
        User applicant = entityFacade.getUser(payload.getJoinUserId());

        StudyMember leader = studyMemberService.findLeaderByStudyId(payload.getStudyId());
        List<Long> recipientIds = List.of(leader.getUser().getId());

        String title = ms.getMessage(JOIN_REQUESTED_MESSAGE_TITLE_CODE, new String[]{study.getTitle()}, Locale.getDefault());
        String content = ms.getMessage(JOIN_REQUESTED_MESSAGE_CONTENT_CODE, new String[]{applicant.getNickname()}, Locale.getDefault());

        StudyJoinedPayload dataPayload = StudyJoinedPayload.requested(study.getId(), payload.getStudyJoinId());

        return NotificationDetails.builder()
                .recipientIds(recipientIds)
                .title(title)
                .content(content)
                .eventType(type)
                .dataPayload(dataPayload)
                .build();
    }

    @Override
    public EventType support() {
        return EventType.STUDY_JOIN_REQUESTED;
    }
}

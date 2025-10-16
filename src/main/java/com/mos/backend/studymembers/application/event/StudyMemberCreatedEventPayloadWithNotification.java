package com.mos.backend.studymembers.application.event;

import com.mos.backend.common.event.NotificationPayload;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class StudyMemberCreatedEventPayloadWithNotification implements NotificationPayload {
    // 새로운 멤버의 userId
    private Long newMemberUserId;
    private Long studyId;
}

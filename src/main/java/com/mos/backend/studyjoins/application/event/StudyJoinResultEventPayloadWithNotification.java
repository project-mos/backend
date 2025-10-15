package com.mos.backend.studyjoins.application.event;

import com.mos.backend.common.event.NotificationPayload;
import com.mos.backend.studyjoins.entity.StudyJoinStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class StudyJoinResultEventPayloadWithNotification implements NotificationPayload {
    private Long joinUserId;
    private Long studyId;
    private Long studyJoinId;
    private StudyJoinStatus status;
}

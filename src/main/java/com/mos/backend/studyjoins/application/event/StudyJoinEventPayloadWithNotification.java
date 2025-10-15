package com.mos.backend.studyjoins.application.event;

import com.mos.backend.common.event.NotificationPayload;
import com.mos.backend.hotstudies.entity.HotStudyEventType;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class StudyJoinEventPayloadWithNotification implements NotificationPayload {
    private Long joinUserId;
    private HotStudyEventType type;
    private Long studyId;
    private Long studyJoinId;
    private String studyName;
}
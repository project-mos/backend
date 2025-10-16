package com.mos.backend.studies.application.event;

import com.mos.backend.common.event.NotificationPayload;
import com.mos.backend.common.event.Payload;
import com.mos.backend.hotstudies.entity.HotStudyEventType;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor
@Getter
public class StudyDeletedEventPayloadWithNotification implements NotificationPayload {
    HotStudyEventType type;
    private Long userId;
    private List<Long> recipientIds;
    private Long studyId;
}
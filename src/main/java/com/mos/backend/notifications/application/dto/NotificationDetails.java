package com.mos.backend.notifications.application.dto;

import com.mos.backend.common.event.EventType;
import com.mos.backend.notifications.application.dto.payload.DataPayload;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class NotificationDetails {
    private List<Long> recipientIds;
    private EventType eventType;
    private String title;
    private String content;
    private DataPayload dataPayload;

    @Builder.Default
    private final boolean loggable = true;
}

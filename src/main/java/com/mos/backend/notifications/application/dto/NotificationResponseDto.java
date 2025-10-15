package com.mos.backend.notifications.application.dto;

import com.mos.backend.common.event.EventType;
import com.mos.backend.notifications.application.dto.payload.DataPayload;
import com.mos.backend.notifications.entity.NotificationLog;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class NotificationResponseDto {
    private Long notificationId;

    private Long recipientId;

    private EventType type;

    private String title;

    private String content;

    private DataPayload dataPayload;
    private boolean isRead;
    private LocalDateTime createdAt;

    public NotificationResponseDto(NotificationLog notificationLog) {
        this.notificationId = notificationLog.getId();
        this.recipientId = notificationLog.getRecipient().getId();
        this.type = notificationLog.getType();
        this.title = notificationLog.getTitle();
        this.content = notificationLog.getContent();
        this.dataPayload = notificationLog.getPayload();
        this.isRead = notificationLog.isRead();
        this.createdAt = notificationLog.getCreatedAt();
    }
}

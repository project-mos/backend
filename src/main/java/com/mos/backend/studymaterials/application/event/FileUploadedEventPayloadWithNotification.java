package com.mos.backend.studymaterials.application.event;

import com.mos.backend.common.event.NotificationPayload;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class FileUploadedEventPayloadWithNotification implements NotificationPayload {
    private Long uploaderId;
    private Long studyId;
    private String originalFilename;
}

package com.mos.backend.studymaterials.application.event;

import com.mos.backend.common.event.NotificationPayload;
import com.mos.backend.common.event.Payload;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class FileUploadFailedEventPayloadWithNotification implements NotificationPayload {
    private Long uploaderId;
    private Long studyId;
    private final String filePath;
    private final String originalFilename;
}

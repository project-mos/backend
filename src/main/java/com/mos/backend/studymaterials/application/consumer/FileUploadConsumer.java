package com.mos.backend.studymaterials.application.consumer;

import com.mos.backend.common.event.Event;
import com.mos.backend.studymaterials.application.StudyMaterialService;
import com.mos.backend.studymaterials.application.event.FileUploadFailedEventPayloadWithNotification;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FileUploadConsumer {

    private final StudyMaterialService studyMaterialService;

    @EventListener
    public void handleFileUploadFailedEvent(Event<FileUploadFailedEventPayloadWithNotification> event) {
        FileUploadFailedEventPayloadWithNotification payload = event.getPayload();
        studyMaterialService.delete(payload.getFilePath());
    }
}

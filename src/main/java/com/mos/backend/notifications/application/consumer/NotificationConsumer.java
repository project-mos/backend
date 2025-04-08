package com.mos.backend.notifications.application.consumer;

import com.mos.backend.common.event.Event;
import com.mos.backend.common.event.EventType;
import com.mos.backend.common.event.NotificationPayload;
import com.mos.backend.notifications.application.eventhandler.NotificationEventHandlerDispatcher;
import com.mos.backend.notifications.application.NotificationLogService;
import com.mos.backend.notifications.application.sending.SendingService;
import com.mos.backend.notifications.application.eventhandler.NotificationEventHandler;
import com.mos.backend.notifications.application.dto.NotificationDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class NotificationConsumer {

    private final NotificationEventHandlerDispatcher notificationEventHandlerDispatcher;
    private final NotificationLogService notificationLogService;
    private final SendingService sendingService;

    @EventListener
    public <T extends NotificationPayload> void handleNotificationEvent(Event<T> event) {
        NotificationEventHandler handler = notificationEventHandlerDispatcher.findNotificationHandler(event.getEventType());
        List<NotificationDetails> notificationDetailsList = handler.prepareDetails(event.getEventType(), event.getPayload());
        notificationDetailsList.forEach(n -> {
            notificationLogService.create(Long.parseLong(n.getRecipientId()), EventType.valueOf(n.getDataPayloadDto().getType()), n.getTitle(), n.getContent());
            sendingService.sendMessage(Long.parseLong(n.getRecipientId()), n.getTitle(), n.getContent(), n.getDataPayloadDto());
        });
    }
}

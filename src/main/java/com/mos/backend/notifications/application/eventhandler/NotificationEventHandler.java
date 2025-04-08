package com.mos.backend.notifications.application.eventhandler;

import com.mos.backend.common.event.EventType;
import com.mos.backend.common.event.NotificationPayload;
import com.mos.backend.notifications.application.dto.NotificationDetails;

import java.util.List;

public interface NotificationEventHandler<T extends NotificationPayload> {
    List<NotificationDetails> prepareDetails(EventType type, T payload);
    EventType support();
}


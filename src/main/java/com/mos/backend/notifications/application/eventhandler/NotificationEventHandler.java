package com.mos.backend.notifications.application.eventhandler;

import com.mos.backend.common.event.EventType;
import com.mos.backend.common.event.NotificationPayload;
import com.mos.backend.notifications.application.dto.NotificationDetails;

public interface NotificationEventHandler<T extends NotificationPayload> {
    NotificationDetails prepareDetails(EventType type, T payload);
    EventType support();
}


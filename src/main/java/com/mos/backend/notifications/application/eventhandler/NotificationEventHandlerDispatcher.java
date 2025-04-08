package com.mos.backend.notifications.application.eventhandler;

import com.mos.backend.common.event.EventType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class NotificationEventHandlerDispatcher {
    private Map<EventType, NotificationEventHandler> notificationHandlerMap = new EnumMap<>(EventType.class);

    @Autowired
    public NotificationEventHandlerDispatcher(List<NotificationEventHandler> notificationHandlerList) {
        notificationHandlerList.forEach(notificationEventHandler ->
                notificationHandlerMap.put(notificationEventHandler.support(), notificationEventHandler));
    }

    public NotificationEventHandler findNotificationHandler(EventType eventType) {
        if (!support(eventType)) {
            log.error("적절한 핸들러 반환 실패");
            throw new IllegalArgumentException("cannot find proper handler");
        } return notificationHandlerMap.get(eventType);
    }

    private boolean support(EventType eventType) {
        return notificationHandlerMap.containsKey(eventType);
    }
}

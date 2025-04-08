package com.mos.backend.notifications.application;

import com.mos.backend.common.event.EventType;
import com.mos.backend.common.infrastructure.EntityFacade;
import com.mos.backend.notifications.entity.NotificationLog;
import com.mos.backend.notifications.infrastructure.notificationlog.NotificationLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NotificationLogService {

    private final NotificationLogRepository notificationLogRepository;
    private final EntityFacade entityFacade;

    @Transactional
    public void  create (Long recipientId, EventType eventType, String title, String content) {
        notificationLogRepository.save(NotificationLog.create(entityFacade.getUser(recipientId), eventType, title, content));
    }

    @Transactional
    public void read(Long notificationLogId) {
        NotificationLog notificationLog = entityFacade.getNotificationLog(notificationLogId);
        notificationLog.read();
    }
}

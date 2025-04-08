package com.mos.backend.notifications.infrastructure.notificationlog;

import com.mos.backend.notifications.entity.NotificationLog;

import java.util.Optional;

public interface NotificationLogRepository {
    void save(NotificationLog notificationLog);

    Optional<NotificationLog> findById(Long notificationId);
}

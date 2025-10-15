package com.mos.backend.notifications.infrastructure.notificationlog;

import com.mos.backend.notifications.entity.NotificationLog;
import com.mos.backend.notifications.entity.NotificationReadStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface NotificationLogRepository {
    void save(NotificationLog notificationLog);

    Optional<NotificationLog> findById(Long notificationId);

    Integer getUnreadCount(Long userId);

    Page<NotificationLog> getNotifications(Pageable pageable, Long userId, NotificationReadStatus readStatus);

    void saveAll(List<NotificationLog> logs);
}

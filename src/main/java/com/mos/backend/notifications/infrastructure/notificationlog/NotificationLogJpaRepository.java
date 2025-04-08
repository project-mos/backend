package com.mos.backend.notifications.infrastructure.notificationlog;

import com.mos.backend.notifications.entity.NotificationLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationLogJpaRepository extends JpaRepository<NotificationLog, Long>{
}

package com.mos.backend.notifications.infrastructure.notificationlog;

import com.mos.backend.notifications.entity.NotificationLog;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@RequiredArgsConstructor
@Repository
public class NotificationLogRepositoryImpl implements NotificationLogRepository{
    private final NotificationLogJpaRepository notificationLogJpaRepository;

    @Override
    public void save(NotificationLog notificationLog) {
        notificationLogJpaRepository.save(notificationLog);
    }

    @Override
    public Optional<NotificationLog> findById(Long notificationId) {
        return notificationLogJpaRepository.findById(notificationId);
    }
}

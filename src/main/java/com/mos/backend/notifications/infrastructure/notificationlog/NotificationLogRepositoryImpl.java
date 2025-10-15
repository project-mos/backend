package com.mos.backend.notifications.infrastructure.notificationlog;

import com.mos.backend.notifications.entity.NotificationLog;
import com.mos.backend.notifications.entity.NotificationReadStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Repository
public class NotificationLogRepositoryImpl implements NotificationLogRepository{
    private final NotificationLogJpaRepository notificationLogJpaRepository;
    private final NotificationLogQueryDslRepository notificationLogQueryDslRepository;

    @Override
    public void save(NotificationLog notificationLog) {
        notificationLogJpaRepository.save(notificationLog);
    }

    @Override
    public Optional<NotificationLog> findById(Long notificationId) {
        return notificationLogJpaRepository.findById(notificationId);
    }

    @Override
    public Integer getUnreadCount(Long userId) {
        return notificationLogJpaRepository.countNotificationLogByRecipientIdAndIsReadIsFalse(userId);
    }

    @Override
    public Page<NotificationLog> getNotifications(Pageable pageable, Long userId, NotificationReadStatus readStatus) {
        return notificationLogQueryDslRepository.getNotificationLogs(pageable, userId, readStatus);
    }

    @Override
    public void saveAll(List<NotificationLog> logs) {
        notificationLogJpaRepository.saveAll(logs);
    }
}

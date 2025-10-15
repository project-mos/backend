package com.mos.backend.notifications.application;

import com.mos.backend.common.event.EventType;
import com.mos.backend.common.infrastructure.EntityFacade;
import com.mos.backend.notifications.application.dto.NotificationDetails;
import com.mos.backend.notifications.application.dto.NotificationListResponseDto;
import com.mos.backend.notifications.application.dto.NotificationResponseDto;
import com.mos.backend.notifications.application.dto.NotificationUnreadCountDto;
import com.mos.backend.notifications.application.dto.payload.DataPayload;
import com.mos.backend.notifications.entity.NotificationLog;
import com.mos.backend.notifications.entity.NotificationReadStatus;
import com.mos.backend.notifications.infrastructure.notificationlog.NotificationLogRepository;
import com.mos.backend.users.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
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
    public void  create (Long recipientId, EventType eventType, String title, String content, DataPayload payload) {
        notificationLogRepository.save(
                NotificationLog.builder()
                        .recipient(entityFacade.getUser(recipientId))
                        .type(eventType)
                        .title(title)
                        .content(content)
                        .payload(payload)
                        .build()
        );
    }

    /**
     * 여러 수신자에 대한 알림 로그를 한 번에 저장
     * @param details 상세 정보 묶음
     */
    @Transactional
    public void saveAll(NotificationDetails details) {
        List<NotificationLog> logs = details.getRecipientIds().stream()
                .map(recipientId -> {
                    User recipient = entityFacade.getUser(recipientId);
                    return NotificationLog.builder()
                            .recipient(recipient)
                            .type(details.getEventType())
                            .title(details.getTitle())
                            .content(details.getContent())
                            .payload(details.getDataPayload())
                            .build();
                })
                .toList();
        notificationLogRepository.saveAll(logs);
    }

    /**
     * 알림 상세 조회하며 읽음 체크
     * @param notificationLogId 읽을 알림의 아이디
     */
    @Transactional
    @PostAuthorize("#returnObject.recipientId == authentication.principal")
    public NotificationResponseDto read(Long notificationLogId) {
        NotificationLog notificationLog = entityFacade.getNotificationLog(notificationLogId);
        notificationLog.read();
        return new NotificationResponseDto(notificationLog);
    }

    /**
     * 사용자가 읽지 않은 알림 수 조회
     * @param userId 읽지 않은 알림의 수를 조회할 사용자 아이디
     */
    @Transactional
    @PreAuthorize("hasRole('ADMIN') or authentication.principal == #userId")
    public NotificationUnreadCountDto getUnreadCount(Long userId) {
        Integer unreadCount = notificationLogRepository.getUnreadCount(userId);
        return new NotificationUnreadCountDto(unreadCount);
    }

    /**
     * 전체 알림 조회
     * @param pageable
     * @param userId 조회할 유저 아이디
     * @param status 읽음 상태 필터링
     */
    @Transactional
    @PreAuthorize("hasRole('ADMIN') or authentication.principal == #userId")
    public NotificationListResponseDto getNotifications(
            Pageable pageable,
            Long userId,
            NotificationReadStatus status
    ) {
        Page<NotificationLog> notifications = notificationLogRepository.getNotifications(pageable, userId, status);
        Page<NotificationResponseDto> dtos = notifications.map(NotificationResponseDto::new);
        return new NotificationListResponseDto(
                notifications.getTotalElements(),
                notifications.getNumber(),
                notifications.getTotalPages(),
                dtos.getContent()
        );
    }
}

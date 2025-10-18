package com.mos.backend.notifications.application.consumer;

import com.mos.backend.common.aop.LogOnException;
import com.mos.backend.common.event.Event;
import com.mos.backend.common.event.EventType;
import com.mos.backend.common.event.NotificationPayload;
import com.mos.backend.notifications.application.eventhandler.NotificationEventHandlerDispatcher;
import com.mos.backend.notifications.application.NotificationLogService;
import com.mos.backend.notifications.application.sending.SendingService;
import com.mos.backend.notifications.application.eventhandler.NotificationEventHandler;
import com.mos.backend.notifications.application.dto.NotificationDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationConsumer {

    private final NotificationEventHandlerDispatcher dispatcher;
    private final NotificationLogService notificationLogService;
    private final SendingService sendingService;

    @TransactionalEventListener
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @LogOnException
    public <T extends NotificationPayload> void handleNotificationEvent(Event<T> event) {
        if (event.getEventType() == EventType.FILE_UPLOADED || event.getEventType() == EventType.FILE_UPLOAD_FAILED) {
            return;
        }
        log.info("Received notification event after commit: {}", event.getEventType());
        processNotification(event);
    }

    @EventListener
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @LogOnException
    public <T extends NotificationPayload> void handleAsyncNotificationEvent(Event<T> event) {
        if (event.getEventType() != EventType.FILE_UPLOADED && event.getEventType() != EventType.FILE_UPLOAD_FAILED) {
            return;
        }
        log.info("Received async event: {}", event.getEventType());
        processNotification(event);
    }


    private <T extends NotificationPayload> void processNotification(Event<T> event) {
        // 해당 이벤트를 처리할 handler 찾기
        NotificationEventHandler<T> handler = dispatcher.findNotificationHandler(event.getEventType());

        // handler를 통해 푸시 알림 발송을 위한 데이터 모음(객체) 생성
        NotificationDetails details = handler.prepareDetails(event.getEventType(), event.getPayload());

        List<Long> recipientIds = details.getRecipientIds();
        if (recipientIds == null || recipientIds.isEmpty()) {
            log.warn("No recipients for notification event: {}", event.getEventType());
            return;
        }

        // 푸시 알림 발송
        sendingService.sendMulticastMessage(
                recipientIds,
                details.getTitle(),
                details.getContent(),
                details.getDataPayload()
        );

        if (details.isLoggable()) {
            notificationLogService.saveAll(details);
        }
        log.info("Successfully processed notification event {} for {} recipients.", event.getEventType(), recipientIds.size());
    }
}

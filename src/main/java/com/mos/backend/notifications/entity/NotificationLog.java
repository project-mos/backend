package com.mos.backend.notifications.entity;

import com.mos.backend.common.entity.BaseTimeEntity;
import com.mos.backend.common.event.EventType;
import com.mos.backend.users.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static jakarta.persistence.EnumType.STRING;
import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

@Entity
@Table(name = "notification_logs")
@Getter
@NoArgsConstructor(access = PROTECTED)
public class NotificationLog extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "recipient_id", nullable = false)
    private User recipient;

    @Enumerated(value = STRING)
    @Column(nullable = false)
    private EventType type;

    @Column(nullable = false)
    private String title;

    @Column(nullable = true)
    private String content;

    @Column
    private boolean isRead = false;

    public static NotificationLog create(User recipient, EventType type,String title, String content) {
        NotificationLog notificationLog = new NotificationLog();
        notificationLog.recipient = recipient;
        notificationLog.type = type;
        notificationLog.title = title;
        notificationLog.content = content;
        return notificationLog;
    }

    public void read() {
        isRead = true;
    }
}

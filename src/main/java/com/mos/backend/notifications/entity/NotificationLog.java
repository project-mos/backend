package com.mos.backend.notifications.entity;

import com.mos.backend.common.entity.BaseTimeEntity;
import com.mos.backend.common.event.EventType;
import com.mos.backend.notifications.application.dto.payload.DataPayload;
import com.mos.backend.notifications.infrastructure.persistence.DataPayloadConverter;
import com.mos.backend.users.entity.User;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;

import static jakarta.persistence.EnumType.STRING;
import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;
import static org.hibernate.annotations.OnDeleteAction.CASCADE;

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
    @OnDelete(action = CASCADE)
    private User recipient;

    @Enumerated(value = STRING)
    @Column(nullable = false)
    private EventType type;

    @Column(nullable = false)
    private String title;

    @Column(nullable = true)
    private String content;

    @Column(nullable = false, name = "is_read")
    private boolean isRead = false;

    @Convert(converter = DataPayloadConverter.class)
    @Column(columnDefinition = "TEXT", nullable = false)
    private DataPayload payload;

    @Builder
    public NotificationLog (User recipient, EventType type,String title, String content, DataPayload payload) {
        this.recipient = recipient;
        this.type = type;
        this.title = title;
        this.content = content;
        this.payload = payload;
    }

    public void read() {
        isRead = true;
    }
}

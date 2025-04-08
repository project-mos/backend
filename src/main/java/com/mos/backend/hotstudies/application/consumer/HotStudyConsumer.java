package com.mos.backend.hotstudies.application.consumer;

import com.mos.backend.common.event.Event;
import com.mos.backend.hotstudies.application.HotStudyService;
import com.mos.backend.studies.application.event.StudyDeletedEventPayload;
import com.mos.backend.studies.application.event.StudyViewedEventPayload;
import com.mos.backend.studyjoins.application.event.StudyJoinEventPayloadWithNotification;
import com.mos.backend.userstudylikes.application.event.StudyLikeEventPayload;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

import static org.springframework.transaction.event.TransactionPhase.BEFORE_COMMIT;

@RequiredArgsConstructor
@Component
public class HotStudyConsumer {

    private final HotStudyService hotStudyService;

    @TransactionalEventListener(phase = BEFORE_COMMIT)
    public void handleStudyViewedEvent(Event<StudyViewedEventPayload> event) {
        StudyViewedEventPayload payload = event.getPayload();
        hotStudyService.handleEvent(payload.getType(), payload.getStudyId());
    }

    @TransactionalEventListener(phase = BEFORE_COMMIT)
    public void handleStudyJoinEvent(Event<StudyJoinEventPayloadWithNotification> event) {
        StudyJoinEventPayloadWithNotification payload = event.getPayload();
        hotStudyService.handleEvent(payload.getType(), payload.getStudyId());
    }

    @TransactionalEventListener(phase = BEFORE_COMMIT)
    public void handleStudyLikeEvent(Event<StudyLikeEventPayload> event) {
        StudyLikeEventPayload payload = event.getPayload();
        hotStudyService.handleEvent(payload.getType(), payload.getStudyId());
    }

    @TransactionalEventListener(phase = BEFORE_COMMIT)
    public void handleStudyDeletedEvent(Event<StudyDeletedEventPayload> event) {
        StudyDeletedEventPayload payload = event.getPayload();
        hotStudyService.handleEvent(payload.getType(), payload.getStudyId());
    }
}
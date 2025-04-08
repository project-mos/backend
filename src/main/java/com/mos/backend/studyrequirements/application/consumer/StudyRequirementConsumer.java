package com.mos.backend.studyrequirements.application.consumer;

import com.mos.backend.common.event.Event;
import com.mos.backend.studies.application.event.StudyCreatedEventPayload;
import com.mos.backend.studyrequirements.application.StudyRequirementService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

import static org.springframework.transaction.event.TransactionPhase.BEFORE_COMMIT;

@Component
@RequiredArgsConstructor
@Slf4j
public class StudyRequirementConsumer {

    private final StudyRequirementService studyRequirementService;

    @TransactionalEventListener(phase = BEFORE_COMMIT)
    public void handleStudyCreatedEvent(Event<StudyCreatedEventPayload> event) {
        StudyCreatedEventPayload payload = event.getPayload();
        studyRequirementService.createOrUpdateOrDelete(payload.getStudyId(), payload.getRequestDto().getRequirements());
    }
}

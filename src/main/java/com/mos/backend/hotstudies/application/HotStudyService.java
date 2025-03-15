package com.mos.backend.hotstudies.application;

import com.mos.backend.hotstudies.application.hotstudyeventhandler.EventHandler;
import com.mos.backend.hotstudies.entity.HotStudyEventType;
import com.mos.backend.hotstudies.infrastructure.HotStudyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class HotStudyService {
    private final HotStudyEventHandlerDispatcher hotStudyEventHandlerDispatcher;
    private final HotStudyRepository hotStudyRepository;
    private final HotStudyScoreUpdater hotStudyScoreUpdater;

    public void handleEvent(HotStudyEventType type, Long studyId) {
        EventHandler hotStudyEventHandler = hotStudyEventHandlerDispatcher.getHotStudyEventHandler(type);
        hotStudyEventHandler.handle(studyId, type);
        if (isCreateOrUpdateEvent(type)) {
            hotStudyScoreUpdater.update(studyId);
        } else {
            hotStudyRepository.remove(studyId);
        }

    }

    private boolean isCreateOrUpdateEvent(HotStudyEventType type) {
        return !HotStudyEventType.DELETE.equals(type);
    }


}

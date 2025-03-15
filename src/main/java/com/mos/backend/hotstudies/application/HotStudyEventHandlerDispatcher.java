package com.mos.backend.hotstudies.application;

import com.mos.backend.common.exception.MosException;
import com.mos.backend.hotstudies.application.hotstudyeventhandler.EventHandler;
import com.mos.backend.hotstudies.entity.HotStudyEventType;
import com.mos.backend.studies.entity.exception.StudyErrorCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class HotStudyEventHandlerDispatcher {
    protected static final Map<List<HotStudyEventType>, EventHandler> hotStudyEventHandlerMap = new HashMap<>();

    @Autowired
    public HotStudyEventHandlerDispatcher(List<EventHandler> eventHandlerList) {
        for (EventHandler eventHandler : eventHandlerList) {
            hotStudyEventHandlerMap.put(eventHandler.supportList(), eventHandler);
        }
    }

    public EventHandler getHotStudyEventHandler(HotStudyEventType eventType) {
        if (!supports(eventType)) {
            throw new MosException(StudyErrorCode.INTERNAL_SERVER_ERROR);
        }
        for (Map.Entry<List<HotStudyEventType>, EventHandler> entry : hotStudyEventHandlerMap.entrySet()) {
            if (entry.getKey().contains(eventType)) {
                return entry.getValue();
            }
        }
        return null;
    }

    private boolean supports(HotStudyEventType eventType) {
        for (List<HotStudyEventType> supportedEventTypes : hotStudyEventHandlerMap.keySet()) {
            if (supportedEventTypes.contains(eventType)) {
                return true;
            }
        }
        return false;
    }
}

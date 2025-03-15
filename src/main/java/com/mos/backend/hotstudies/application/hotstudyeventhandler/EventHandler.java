package com.mos.backend.hotstudies.application.hotstudyeventhandler;

import com.mos.backend.hotstudies.entity.HotStudyEventType;

import java.util.List;

public interface EventHandler {
    void handle(Long studyId, HotStudyEventType type);
    List<HotStudyEventType> supportList();
}

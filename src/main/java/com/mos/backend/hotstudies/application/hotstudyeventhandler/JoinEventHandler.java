package com.mos.backend.hotstudies.application.hotstudyeventhandler;

import com.mos.backend.common.utils.TimeCalculator;
import com.mos.backend.hotstudies.entity.HotStudyEventType;
import com.mos.backend.hotstudies.infrastructure.HotStudyJoinRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JoinEventHandler implements EventHandler{

    private final HotStudyJoinRepository hotStudyJoinRepository;

    @Override
    public void handle(Long studyId, HotStudyEventType type) {
        hotStudyJoinRepository.createOrUpdate(studyId, type.getValue(), TimeCalculator.calculateDurationToMidNight());
    }

    @Override
    public List<HotStudyEventType> supportList() {
        return new ArrayList<>(List.of(HotStudyEventType.JOIN, HotStudyEventType.JOIN_CANCEL));
    }
}

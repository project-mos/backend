package com.mos.backend.hotstudies.application;

import com.mos.backend.hotstudies.infrastructure.HotStudyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
@RequiredArgsConstructor
public class HotStudyScoreUpdater {

    private final HotStudyRepository hotStudyRepository;
    private final HotStudyScoreCalculator hotStudyScoreCalculator;

    private static final long HOT_STUDY_COUNT = 10;
    private static final Duration HOT_STUDY_TTL = Duration.ofDays(3);

    public void update(Long studyId) {
        long score = hotStudyScoreCalculator.calculate(studyId);
        hotStudyRepository.add(studyId, score, HOT_STUDY_COUNT, HOT_STUDY_TTL);
    }
}

package com.mos.backend.hotstudies.application;

import com.mos.backend.hotstudies.infrastructure.HotStudyJoinRepository;
import com.mos.backend.hotstudies.infrastructure.HotStudyLikeRepository;
import com.mos.backend.hotstudies.infrastructure.HotStudyViewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class HotStudyScoreCalculator {
    private final HotStudyViewRepository hotStudyViewRepository;
    private final HotStudyLikeRepository hotStudyLikeRepository;
    private final HotStudyJoinRepository hotStudyJoinRepository;

    private static final long STUDY_VIEW_COUNT_WEIGHT = 1;
    private static final long STUDY_LIKE_COUNT_WEIGHT = 3;
    private static final long STUDY_JOIN_COUNT_WEIGHT = 5;

    public long calculate(Long studyId) {
        Long studyViewCount = hotStudyViewRepository.read(studyId);
        Long studyLikeCount = hotStudyLikeRepository.read(studyId);
        Long studyJointCount = hotStudyJoinRepository.read(studyId);


        return studyViewCount * STUDY_VIEW_COUNT_WEIGHT
                + studyLikeCount * STUDY_LIKE_COUNT_WEIGHT
                + studyJointCount * STUDY_JOIN_COUNT_WEIGHT;
    }
}

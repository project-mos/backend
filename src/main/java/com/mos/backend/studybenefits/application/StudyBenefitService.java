package com.mos.backend.studybenefits.application;

import com.mos.backend.common.exception.MosException;
import com.mos.backend.studies.entity.Study;
import com.mos.backend.studies.entity.exception.StudyErrorCode;
import com.mos.backend.studies.infrastructure.StudyRepository;
import com.mos.backend.studybenefits.entity.StudyBenefit;
import com.mos.backend.studybenefits.infrastructure.StudyBenefitRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class StudyBenefitService {

    private final StudyBenefitRepository studyBenefitRepository;
    private final StudyRepository studyRepository;

    public void create(Long studyId, List<String> contents) {
        if (contents.isEmpty()) {
            return;
        }

        Study study = getStudyById(studyId);
        List<StudyBenefit> studyBenefitList = contents.stream().map(c -> StudyBenefit.create(study, c)).toList();
        studyBenefitRepository.saveAll(studyBenefitList);
    }

    private Study getStudyById(Long studyId) {
        return studyRepository.findById(studyId).orElseThrow(() -> new MosException(StudyErrorCode.STUDY_NOT_FOUND));
    }


}

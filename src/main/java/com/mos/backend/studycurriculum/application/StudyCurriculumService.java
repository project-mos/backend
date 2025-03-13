package com.mos.backend.studycurriculum.application;

import com.mos.backend.common.exception.MosException;
import com.mos.backend.studies.entity.Study;
import com.mos.backend.studies.entity.exception.StudyErrorCode;
import com.mos.backend.studies.infrastructure.StudyRepository;
import com.mos.backend.studycurriculum.entity.StudyCurriculum;
import com.mos.backend.studycurriculum.infrastructure.StudyCurriculumRepository;
import com.mos.backend.studycurriculum.presentation.requestdto.StudyCurriculumCreateRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StudyCurriculumService {

    private final StudyCurriculumRepository studyCurriculumRepository;
    private final StudyRepository studyRepository;

    @Transactional
    public void create(Long studyId, List<StudyCurriculumCreateRequestDto> requestDtoList) {

        if (requestDtoList.isEmpty()) {
            return;
        }

        Study study = getStudyById(studyId);

        List<StudyCurriculum> studyCurriculumList = requestDtoList.stream().map(c ->
                StudyCurriculum.create(study, c.getTitle(), c.getSectionId(), c.getContent())).toList();

        studyCurriculumRepository.saveAll(studyCurriculumList);
    }

    private Study getStudyById(Long studyId) {
        return studyRepository.findById(studyId).orElseThrow(() -> new MosException(StudyErrorCode.STUDY_NOT_FOUND));
    }
}

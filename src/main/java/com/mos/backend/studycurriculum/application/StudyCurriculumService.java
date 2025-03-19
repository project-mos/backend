package com.mos.backend.studycurriculum.application;

import com.mos.backend.common.exception.MosException;
import com.mos.backend.common.infrastructure.EntityFacade;
import com.mos.backend.studies.entity.Study;
import com.mos.backend.studycurriculum.application.responsedto.StudyCurriculumResponseDto;
import com.mos.backend.studycurriculum.entity.StudyCurriculum;
import com.mos.backend.studycurriculum.entity.exception.StudyCurriculumErrorCode;
import com.mos.backend.studycurriculum.infrastructure.StudyCurriculumRepository;
import com.mos.backend.studycurriculum.presentation.requestdto.StudyCurriculumCreateRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StudyCurriculumService {

    private final StudyCurriculumRepository studyCurriculumRepository;
    private final EntityFacade entityFacade;

    /**
     * StudyCurriculum 생성 | 수정 | 삭제
     */

    @Transactional
    public List<StudyCurriculumResponseDto> createOrUpdateOrDelete(Long studyId, List<StudyCurriculumCreateRequestDto> requestDtoList) {
        validateRequest(requestDtoList);
        Study study = entityFacade.getStudy(studyId);

        List<StudyCurriculum> curriculumList = studyCurriculumRepository.findAllByStudy(study);

        // 요청에 없는 기존 항목은 삭제
        List<StudyCurriculum> deleteCurriculumList = getDeleteCurriculumList(requestDtoList, curriculumList);
        if (!deleteCurriculumList.isEmpty()) {
            studyCurriculumRepository.deleteAll(deleteCurriculumList);
        }

        // id가 없으면 새로 저장, 있으면 업데이트
        requestDtoList.forEach(requestDto -> {
            if (requestDto.getId() == null) {
                studyCurriculumRepository.save(StudyCurriculum.create(study, requestDto.getTitle(), requestDto.getSectionId(), requestDto.getContent()));
            } else {
                StudyCurriculum studyCurriculum = findByIdAndStudy(requestDto.getId(), study);
                studyCurriculum.update(requestDto.getTitle(), requestDto.getSectionId(), requestDto.getContent());
            }
        });
        return getAll(studyId);
    }

    /**
     * StudyCurriculum 목록 조회
     */

    public List<StudyCurriculumResponseDto> getAll(Long studyId) {
        Study study = entityFacade.getStudy(studyId);
        List<StudyCurriculum> studyCurriculumList = studyCurriculumRepository.findAllByStudy(study);
        studyCurriculumList.sort(Comparator.comparing(StudyCurriculum::getSectionId));
        return studyCurriculumList.stream().map(StudyCurriculumResponseDto::from).toList();
    }

    /**
     * StudyCurriculum 단 건 조회
     */

    public StudyCurriculumResponseDto get(Long studyId, Long studyCurriculumId) {
        Study study = entityFacade.getStudy(studyId);
        StudyCurriculum studyCurriculum = findByIdAndStudy(studyCurriculumId, study);
        return StudyCurriculumResponseDto.from(studyCurriculum);
    }


    private void validateRequest(List<StudyCurriculumCreateRequestDto> requestDtoList) {
        validateSectionNum(requestDtoList);
    }

    private void validateSectionNum(List<StudyCurriculumCreateRequestDto> requestDtoList) {
        Set<Long> sectionIdSet = requestDtoList.stream()
                .map(StudyCurriculumCreateRequestDto::getSectionId)
                .collect(Collectors.toSet());

        if (sectionIdSet.size() != requestDtoList.size()) {
            throw new MosException(StudyCurriculumErrorCode.INVALID_SECTION_ID);
        }
    }

    private List<StudyCurriculum> getDeleteCurriculumList(List<StudyCurriculumCreateRequestDto> requestDtoList, List<StudyCurriculum> curriculumList) {
        List<Long> requestCurriculumIdList = requestDtoList.stream()
                .map(StudyCurriculumCreateRequestDto::getId)
                .filter(Objects::nonNull)
                .toList();
        return curriculumList.stream()
                .filter(b -> !requestCurriculumIdList.contains(b.getId()))
                .toList();

    }

    private StudyCurriculum findByIdAndStudy(Long id, Study study) {
        return studyCurriculumRepository.findByIdAndStudy(id, study).orElseThrow(() -> new MosException(StudyCurriculumErrorCode.STUDY_CURRICULUM_NOT_FOUND));
    }
}

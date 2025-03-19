package com.mos.backend.studycurriculum.presentation.requestdto.controller.api;

import com.mos.backend.studycurriculum.application.StudyCurriculumService;
import com.mos.backend.studycurriculum.application.responsedto.StudyCurriculumResponseDto;
import com.mos.backend.studycurriculum.presentation.requestdto.StudyCurriculumCreateRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class StudyCurriculumController {
    private final StudyCurriculumService studyCurriculumService;

    /**
     * StudyCurriculum 생성 | 수정 | 삭제
     */

    @PostMapping("/studies/{studyId}/curriculums")
    @ResponseStatus(HttpStatus.OK)
    public List<StudyCurriculumResponseDto> createOrUpdateOrDelete(@PathVariable Long studyId, @RequestBody List<StudyCurriculumCreateRequestDto> requestDtoList) {
        return studyCurriculumService.createOrUpdateOrDelete(studyId, requestDtoList);
    }

    /**
     * StudyCurriculum 단 건 조회
     */

    @GetMapping("/studies/{studyId}/curriculums/{curriculumId}")
    @ResponseStatus(HttpStatus.OK)
    public StudyCurriculumResponseDto get(@PathVariable Long studyId, @PathVariable Long curriculumId) {
        return studyCurriculumService.get(studyId, curriculumId);
    }

    /**
     * StudyCurriculum 단 건 조회
     */

    @GetMapping("/studies/{studyId}/curriculums")
    @ResponseStatus(HttpStatus.OK)
    public List<StudyCurriculumResponseDto> getAll(@PathVariable Long studyId) {
        return studyCurriculumService.getAll(studyId);
    }
}

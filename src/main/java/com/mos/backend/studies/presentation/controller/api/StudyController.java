package com.mos.backend.studies.presentation.controller.api;

import com.mos.backend.studies.application.StudyService;
import com.mos.backend.studies.application.responsedto.*;
import com.mos.backend.studies.presentation.requestdto.StudyCreateRequestDto;
import com.mos.backend.studies.presentation.requestdto.StudySubNoticeRequestDto;
import com.mos.backend.studies.presentation.requestdto.StudyUpdateRequestDto;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/studies")
@RequiredArgsConstructor
public class StudyController {

    private static final String BASE_URL = "/studies/";

    private final StudyService studyService;

    /**
     * 스터디 생성
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public StudyCreateResponseDto create(@RequestBody @Valid StudyCreateRequestDto requestDto, @AuthenticationPrincipal Long userId) {
        return studyService.create(userId, requestDto);
    }

    /**
     * 스터디 단 건 조회
     */

    @GetMapping("/{studyId}")
    @ResponseStatus(HttpStatus.OK)
    public StudyResponseDto get(@PathVariable Long studyId, HttpServletRequest httpServletRequest) {
        return studyService.get(studyId, httpServletRequest);
    }

    /**
     * todo
     * 1. liked 추가
     */

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public StudyCardListResponseDto findStudies(
            @AuthenticationPrincipal Long currentUserId,
            Pageable pageable,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String meetingType,
            @RequestParam(required = false) String recruitmentStatus,
            @RequestParam(required = false) String progressStatus,
            @RequestParam(required = false, defaultValue = "false") Boolean liked) {
        return studyService.findStudies(currentUserId, pageable, category, meetingType, recruitmentStatus, progressStatus, liked);
    }

    /**
     * 인기 스터디 조회
     */

    @GetMapping("/hots")
    @ResponseStatus(HttpStatus.OK)
    public List<StudiesResponseDto> getHotStudyList() {
        return studyService.readHotStudies();
    }

    /**
     * 스터디 삭제
     */
    
    @DeleteMapping("/{studyId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("studyId") Long  studyId, @AuthenticationPrincipal Long userId) {
        studyService.delete(userId, studyId);
    }

    /**
     * 스터디 카테고리 목록 조회
     */
    @GetMapping("/categories")
    @ResponseStatus(HttpStatus.OK)
    public StudyCategoriesResponseDto getStudyCategories() {
        return studyService.getStudyCategories();
    }

    /**
     * 스터디 subNotice 수정
     */
    @PostMapping("/{studyId}/sub-notice")
    @ResponseStatus(HttpStatus.OK)
    public StudyResponseDto updateSubNotice(@PathVariable Long studyId, @RequestBody StudySubNoticeRequestDto requestDto) {
        return studyService.updateSubNotice(studyId, requestDto.getSubNotice());
    }

    /**
     * 스터디 수정
     */
    @PatchMapping("/{studyId}")
    @ResponseStatus(HttpStatus.OK)
    public StudyResponseDto update(
            @PathVariable("studyId") Long studyId,
            @RequestBody StudyUpdateRequestDto requestDto,
            @AuthenticationPrincipal Long userId) {
        return studyService.update(userId, studyId, requestDto);
    }
}

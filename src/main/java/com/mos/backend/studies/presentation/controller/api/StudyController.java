package com.mos.backend.studies.presentation.controller.api;

import com.mos.backend.hotstudies.application.HotStudyService;
import com.mos.backend.studies.application.StudyService;
import com.mos.backend.studies.application.responsedto.StudiesResponseDto;
import com.mos.backend.studies.application.responsedto.StudyCardListResponseDto;
import com.mos.backend.studies.application.responsedto.StudyResponseDto;
import com.mos.backend.studies.presentation.requestdto.StudyCreateRequestDto;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
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
    public ResponseEntity<Void> create(@RequestBody @Valid StudyCreateRequestDto requestDto, @AuthenticationPrincipal Long userId) {
        Long studyId = studyService.create(userId, requestDto);
        return ResponseEntity.created(URI.create(BASE_URL + studyId)).build();
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
            Pageable pageable,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String meetingType,
            @RequestParam(required = false) String recruitmentStatus,
            @RequestParam(required = false) String progressStatus,
            @RequestParam(required = false) Boolean liked) {
        return studyService.findStudies(pageable, category, meetingType, recruitmentStatus, progressStatus);
    }

    /**
     * 인기 스터디 조회
     */
    @GetMapping("/hots")
    @ResponseStatus(HttpStatus.OK)
    public List<StudiesResponseDto> getHotStudyList() {
        return studyService.readHotStudies();
    }
}

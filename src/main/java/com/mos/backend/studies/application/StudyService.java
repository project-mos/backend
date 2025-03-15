package com.mos.backend.studies.application;

import com.mos.backend.common.exception.MosException;
import com.mos.backend.common.infrastructure.EntityFacade;
import com.mos.backend.common.utils.ClientInfoExtractor;
import com.mos.backend.common.utils.RandomColorGenerator;
import com.mos.backend.hotstudies.application.HotStudyService;
import com.mos.backend.hotstudies.entity.HotStudyEventType;
import com.mos.backend.hotstudies.infrastructure.HotStudyRepository;
import com.mos.backend.studies.application.responsedto.StudiesResponseDto;
import com.mos.backend.studies.application.responsedto.StudyCardListResponseDto;
import com.mos.backend.studies.application.responsedto.StudyResponseDto;
import com.mos.backend.studies.entity.Category;
import com.mos.backend.studies.entity.MeetingType;
import com.mos.backend.studies.entity.Study;
import com.mos.backend.studies.entity.StudyTag;
import com.mos.backend.studies.entity.exception.StudyErrorCode;
import com.mos.backend.studies.infrastructure.StudyRepository;
import com.mos.backend.studies.presentation.requestdto.StudyCreateRequestDto;
import com.mos.backend.studybenefits.application.StudyBenefitService;
import com.mos.backend.studycurriculum.application.StudyCurriculumService;
import com.mos.backend.studymembers.application.StudyMemberService;
import com.mos.backend.studyquestions.application.StudyQuestionService;
import com.mos.backend.studyrules.application.StudyRuleService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StudyService {

    private final StudyRepository studyRepository;

    private final StudyRuleService studyRuleService;
    private final StudyBenefitService studyBenefitService;
    private final StudyQuestionService studyQuestionService;
    private final StudyCurriculumService studyCurriculumService;
    private final StudyMemberService studyMemberService;
    private final HotStudyRepository hotStudyRepository;
    private final HotStudyService hotStudyService;
    private final EntityFacade entityFacade;
    private final ViewCountService viewCountService;

    /**
     * 스터디 생성
     */

    @Transactional
    public Long create(Long userId, StudyCreateRequestDto requestDto) {
        validateStudyCreateRequest(requestDto);

        Study study = convertToEntity(requestDto);
        Study savedStudy = studyRepository.save(study);

        handleStudyRelations(userId, requestDto, savedStudy.getId());
        return savedStudy.getId();
    }

    /**
     * 스터디 단 건 조회
     */

    @Transactional
    public StudyResponseDto get(long studyId, HttpServletRequest httpServletRequest) {
        String ipAddress = ClientInfoExtractor.extractIpAddress(httpServletRequest);

        viewCountService.handleViewCount(studyId, ipAddress);

        Study study = findStudyById(studyId);
        int studyMemberCount = studyMemberService.countCurrentStudyMember(studyId);

        hotStudyService.handleEvent(HotStudyEventType.VIEW, studyId);
        return StudyResponseDto.from(study, studyMemberCount);
    }

    /**
     * 스터디 다 건 조회
     */

    public StudyCardListResponseDto findStudies(Pageable pageable, String categoryCond, String meetingTypeCond, String recruitmentStatusCond, String progressStatusCond) {
        Page<StudiesResponseDto> studies = studyRepository.findStudies(pageable, categoryCond, meetingTypeCond, recruitmentStatusCond, progressStatusCond);
        return new StudyCardListResponseDto(studies.getTotalElements(), studies.getNumber(), studies.getTotalPages(), studies.getContent());
    }

    /**
     * 인기 스터디 조회
     * @return
     */
    @Transactional
    public List<StudiesResponseDto> readHotStudies() {
        return hotStudyRepository.readAll().stream()
                .map(id -> {
                    try {
                        return this.getHotStudy(id);
                    } catch (MosException e) {
                        hotStudyRepository.remove(id);
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .toList();
    }

    public StudiesResponseDto getHotStudy(Long studyId) {
        Study study = entityFacade.getStudy(studyId);
        int currentStudyMembers = studyMemberService.countCurrentStudyMember(studyId);
        return StudiesResponseDto.from(study, Long.valueOf(currentStudyMembers));
    }

    private void increaseViewCount(long studyId) {
        studyRepository.increaseViewCount(studyId);
    }


    private Study findStudyById(long studyId) {
        return studyRepository.findById(studyId).orElseThrow(() -> new MosException(StudyErrorCode.STUDY_NOT_FOUND));
    }


    private static Study convertToEntity(StudyCreateRequestDto requestDto) {
        return Study.builder()
                .title(requestDto.getTitle())
                .content(requestDto.getContent())
                .maxStudyMemberCount(requestDto.getMaxStudyMemberCount())
                .category(Category.fromDescription(requestDto.getCategory()))
                .schedule(requestDto.getSchedule())
                .recruitmentStartDate(requestDto.getRecruitmentStartDate())
                .recruitmentEndDate(requestDto.getRecruitmentEndDate())
                .tags(StudyTag.fromList(requestDto.getTags()))
                .color(RandomColorGenerator.generateRandomColor())
                .meetingType(MeetingType.fromDescription(requestDto.getMeetingType()))
                .requirements(requestDto.getRequirements())
                .build();
    }

    private void validateStudyCreateRequest(StudyCreateRequestDto requestDto) {
        if (requestDto.getRecruitmentStartDate().isAfter(requestDto.getRecruitmentEndDate())) {
            throw new MosException(StudyErrorCode.INVALID_RECRUITMENT_DATES);
        }
    }

    private void handleStudyRelations(Long userId, StudyCreateRequestDto requestDto, Long savedStudyId) {
        studyRuleService.create(savedStudyId, requestDto.getRules());
        studyBenefitService.createOrUpdateOrDelete(savedStudyId, requestDto.getBenefits());
        studyQuestionService.createOrUpdateOrDelete(savedStudyId, requestDto.getApplicationQuestions());
        studyCurriculumService.create(savedStudyId, requestDto.getCurriculums());
        studyMemberService.create(savedStudyId, userId);
    }
}

package com.mos.backend.studies.application;

import com.mos.backend.common.exception.MosException;
import com.mos.backend.studies.application.responsedto.StudyResponseDto;
import com.mos.backend.studies.entity.Category;
import com.mos.backend.studies.entity.MeetingType;
import com.mos.backend.studies.entity.Study;
import com.mos.backend.studies.entity.StudyTag;
import com.mos.backend.studies.entity.exception.StudyErrorCode;
import com.mos.backend.studies.infrastructure.StudyRepository;
import com.mos.backend.studies.presentation.dto.StudyCreateRequestDto;
import com.mos.backend.studybenefits.application.StudyBenefitService;
import com.mos.backend.studycurriculum.application.StudyCurriculumService;
import com.mos.backend.studymembers.application.StudyMemberService;
import com.mos.backend.studyquestions.application.StudyQuestionService;
import com.mos.backend.studyrules.application.StudyRuleService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
public class StudyService {

    private final StudyRepository studyRepository;

    private final StudyRuleService studyRuleService;
    private final StudyBenefitService studyBenefitService;
    private final StudyQuestionService studyQuestionService;
    private final StudyCurriculumService studyCurriculumService;
    private final StudyMemberService studyMemberService;

    /**
     * 스터디 생성
     */
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

    public StudyResponseDto get(long studyId) {
        increaseViewCount(studyId);
        Study study = findStudyById(studyId);
        return StudyResponseDto.from(study);
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
                .maxParticipantsCount(requestDto.getMaxParticipants())
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
        studyBenefitService.create(savedStudyId, requestDto.getBenefits());
        studyQuestionService.create(savedStudyId, requestDto.getApplicationQuestions());
        studyCurriculumService.create(savedStudyId, requestDto.getCurriculums());
        studyMemberService.create(savedStudyId, userId);
    }
}

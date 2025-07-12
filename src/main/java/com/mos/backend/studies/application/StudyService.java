package com.mos.backend.studies.application;

import com.mos.backend.common.event.Event;
import com.mos.backend.common.event.EventType;
import com.mos.backend.common.exception.MosException;
import com.mos.backend.common.infrastructure.EntityFacade;
import com.mos.backend.common.utils.ClientInfoExtractor;
import com.mos.backend.common.utils.RandomColorGenerator;
import com.mos.backend.hotstudies.entity.HotStudyEventType;
import com.mos.backend.hotstudies.infrastructure.HotStudyRepository;
import com.mos.backend.studies.application.event.StudyCreatedEventPayload;
import com.mos.backend.studies.application.event.StudyDeletedEventPayload;
import com.mos.backend.studies.application.event.StudyUpdatedEventPayload;
import com.mos.backend.studies.application.event.StudyViewedEventPayload;
import com.mos.backend.studies.application.responsedto.*;
import com.mos.backend.studies.entity.*;
import com.mos.backend.studies.entity.exception.StudyErrorCode;
import com.mos.backend.studies.infrastructure.StudyRepository;
import com.mos.backend.studies.presentation.requestdto.StudyCreateRequestDto;
import com.mos.backend.studies.presentation.requestdto.StudyUpdateRequestDto;
import com.mos.backend.studymembers.application.StudyMemberService;
import com.mos.backend.users.application.responsedto.UserStudiesResponseDto;
import com.mos.backend.users.entity.User;
import com.mos.backend.users.entity.exception.UserErrorCode;
import com.querydsl.core.util.StringUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.Querydsl;
import org.springframework.data.querydsl.QuerydslUtils;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class StudyService {

    private final StudyRepository studyRepository;
    private final StudyMemberService studyMemberService;
    private final HotStudyRepository hotStudyRepository;
    private final EntityFacade entityFacade;
    private final ViewCountService viewCountService;
    private final ApplicationEventPublisher eventPublisher;

    /**
     * 스터디 생성
     */

    @Transactional
    public StudyCreateResponseDto create(Long userId, StudyCreateRequestDto requestDto) {
        validateRecruitmentDate(requestDto.getRecruitmentStartDate(), requestDto.getRecruitmentEndDate());

        Study study = convertToEntity(requestDto);
        Study savedStudy = studyRepository.save(study);

        eventPublisher.publishEvent(new Event<>(EventType.STUDY_CREATED, new StudyCreatedEventPayload(userId, requestDto, savedStudy.getId())));
        return new StudyCreateResponseDto(savedStudy.getId());
    }

    /**
     * 스터디 업데이트
     */
    @Transactional
    @PreAuthorize("@studySecurity.isLeaderOrAdmin(#studyId)")
    public StudyResponseDto update(Long userId, Long studyId, StudyUpdateRequestDto requestDto) {
        validateRecruitmentDate(requestDto.getRecruitmentStartDate(), requestDto.getRecruitmentEndDate());
        Study study = entityFacade.getStudy(studyId);
        updateStudy(requestDto, study);
        eventPublisher.publishEvent(new Event<>(EventType.STUDY_UPDATED, new StudyUpdatedEventPayload(userId, requestDto, study.getId())));
        return StudyResponseDto.from(study, studyMemberService.countCurrentStudyMember(studyId));
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

        eventPublisher.publishEvent(new Event<>(EventType.STUDY_VIEWED, new StudyViewedEventPayload(HotStudyEventType.VIEW, studyId)));

        return StudyResponseDto.from(study, studyMemberCount);
    }

    /**
     * 스터디 다 건 조회
     */

    public StudyCardListResponseDto findStudies(Long currentUserId, Pageable pageable, String categoryCond, String meetingTypeCond, String recruitmentStatusCond, String progressStatusCond, boolean liked) {
        if (currentUserId == null && liked) {
            throw new MosException(StudyErrorCode.LOGIN_REQUIRED_FOR_LIKE_FILTER);
        }
        Page<StudiesResponseDto> studies = studyRepository.findStudies(currentUserId, pageable, categoryCond, meetingTypeCond, recruitmentStatusCond, progressStatusCond, liked);
        return new StudyCardListResponseDto(studies.getTotalElements(), studies.getNumber(), studies.getTotalPages(), studies.getContent());
    }

    /**
     * 인기 스터디 목록 조회
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

    /**
     * Study 삭제
     */

    @Transactional
    @PreAuthorize("@studySecurity.isLeaderOrAdmin(#studyId)")
    public void delete(Long userId, Long studyId) {
        Study study = entityFacade.getStudy(studyId);
        studyRepository.delete(study);
        eventPublisher.publishEvent(new Event<>(EventType.STUDY_DELETED, new StudyDeletedEventPayload(HotStudyEventType.DELETE, userId, studyId)));
    }

    /**
     * study sub notice 수정
     */

    @Transactional
    @PreAuthorize("@studySecurity.isLeaderOrAdmin(#studyId)")
    public StudyResponseDto updateSubNotice(Long studyId, String content) {
        Study study = entityFacade.getStudy(studyId);
        study.updateSubNotice(content);
        int studyMemberCount = studyMemberService.countCurrentStudyMember(studyId);
        return StudyResponseDto.from(study, studyMemberCount);
    }


    @Transactional
    public void changeImageToPermanent(Long userId, Long studyId) {
        Study study = entityFacade.getStudy(studyId);
        study.changeImageToPermanent(userId, studyId);
    }

    /**
     * 스터디 카테고리 조회
     */
    public StudyCategoriesResponseDto getStudyCategories() {
        List<String> list = Arrays.stream(Category.values()).map(Category::getDescription).toList();
        return new StudyCategoriesResponseDto(list);
    }

    /**
     * 유저의 참여 중인 스터디 목록 조회
     */
    @PreAuthorize("@userSecurity.isOwnerOrAdmin(#userId)")
    public List<UserStudiesResponseDto> readUserStudies(Long userId, String progressStatus, String participationStatus) {
        User user = entityFacade.getUser(userId);
        return studyRepository.readUserStudies(user, progressStatus, participationStatus);
    }

    /**
     * 인기 스터디 dto 값 채우기
     */
    private StudiesResponseDto getHotStudy(Long studyId) {
        Study study = entityFacade.getStudy(studyId);
        int currentStudyMembers = studyMemberService.countCurrentStudyMember(studyId);
        return StudiesResponseDto.from(study, Long.valueOf(currentStudyMembers));
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
                .build();
    }

    private void validateRecruitmentDate(LocalDate startDate, LocalDate endDate) {
        if (startDate.isAfter(endDate)) {
            throw new MosException(StudyErrorCode.INVALID_RECRUITMENT_DATES);
        }
    }

    public void validateRecruitmentPeriod(Study study) {
        LocalDate now = LocalDate.now();
        if (!(study.getRecruitmentStartDate().isBefore(now) && study.getRecruitmentEndDate().isAfter(now)))
            throw new MosException(StudyErrorCode.NOT_IN_RECRUITMENT_PERIOD);
    }

    public void validateRecruitmentStatus(Study study) {
        if (study.getRecruitmentStatus().equals(RecruitmentStatus.CLOSED))
            throw new MosException(StudyErrorCode.RECRUITMENT_CLOSED);
    }

    public void validateRelation(Study study, Long studyId) {
        if (!study.isRelated(studyId)) {
            throw new MosException(StudyErrorCode.UNRELATED_STUDY);
        }
    }

    private static void updateStudy(StudyUpdateRequestDto requestDto, Study study) {
        study.update(requestDto.getTitle(), requestDto.getCategory(), requestDto.getTags(), requestDto.getMaxStudyMemberCount(), requestDto.getRecruitmentStartDate(), requestDto.getRecruitmentEndDate(), requestDto.getMeetingType(), requestDto.getSchedule(), requestDto.getContent());
    }
}

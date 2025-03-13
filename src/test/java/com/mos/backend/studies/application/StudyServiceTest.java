package com.mos.backend.studies.application;

import com.mos.backend.common.exception.MosException;
import com.mos.backend.studies.application.responsedto.StudyResponseDto;
import com.mos.backend.studies.entity.*;
import com.mos.backend.studies.entity.exception.StudyErrorCode;
import com.mos.backend.studies.infrastructure.StudyRepository;
import com.mos.backend.studies.presentation.dto.StudyCreateRequestDto;
import com.mos.backend.studybenefits.application.StudyBenefitService;
import com.mos.backend.studycurriculum.application.StudyCurriculumService;
import com.mos.backend.studycurriculum.presentation.requestdto.StudyCurriculumCreateRequestDto;
import com.mos.backend.studymembers.application.StudyMemberService;
import com.mos.backend.studyquestions.application.StudyQuestionService;
import com.mos.backend.studyquestions.presentation.requestdto.StudyQuestionCreateRequestDto;
import com.mos.backend.studyrules.application.StudyRuleService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("StudyService 테스트")
class StudyServiceTest {

    @Mock
    private StudyRepository studyRepository;

    @Mock
    private StudyRuleService studyRuleService;

    @Mock
    private StudyBenefitService studyBenefitService;

    @Mock
    private StudyQuestionService studyQuestionService;

    @Mock
    private StudyCurriculumService studyCurriculumService;

    @Mock
    private StudyMemberService studyMemberService;

    @InjectMocks
    private StudyService studyService;

    private StudyCreateRequestDto validRequestDto;
    private Long testUserId;

    @BeforeEach
    void setUp() {
        testUserId = 1L;

        // 유효한 StudyCreateRequestDto 생성
        validRequestDto = new StudyCreateRequestDto();
        validRequestDto.setTitle("Test Study");
        validRequestDto.setContent("Study Content");
        validRequestDto.setCategory("프로그래밍");
        validRequestDto.setMaxStudyMemberCount(5);
        validRequestDto.setRecruitmentStartDate(LocalDate.now());
        validRequestDto.setRecruitmentEndDate(LocalDate.now().plusDays(7));
        validRequestDto.setTags(List.of("Java", "Spring"));
        validRequestDto.setMeetingType("비대면");

        // 필수 리스트 초기화
        validRequestDto.setCurriculums(List.of(
                new StudyCurriculumCreateRequestDto(1L,"Week 1", "Java Basics")
        ));
        validRequestDto.setRules(List.of("Rule 1", "Rule 2"));
        validRequestDto.setBenefits(List.of("Benefit 1"));
        validRequestDto.setApplicationQuestions(List.of(
                new StudyQuestionCreateRequestDto("좋은 질문", 1L, true, "주관식", List.of("A", "B", "C"))
        ));
    }

    @Nested
    @DisplayName("스터디 생성 성공 시나리오")
    class SuccessScenarios {
        @Test
        @DisplayName("정상적인 스터디 생성")
        void createStudy_Success() {
            // Given
            Study mockStudy = Study.builder()
                    .id(1L)
                    .title(validRequestDto.getTitle())
                    .build();

            when(studyRepository.save(any(Study.class))).thenReturn(mockStudy);

            // When
            Long studyId = studyService.create(testUserId, validRequestDto);

            // Then
            assertNotNull(studyId);
            assertEquals(1L, studyId);

            // 연관된 서비스 메서드 호출 검증
            verify(studyRuleService).create(eq(studyId), eq(validRequestDto.getRules()));
            verify(studyBenefitService).create(eq(studyId), eq(validRequestDto.getBenefits()));
            verify(studyQuestionService).create(eq(studyId), eq(validRequestDto.getApplicationQuestions()));
            verify(studyCurriculumService).create(eq(studyId), eq(validRequestDto.getCurriculums()));
            verify(studyMemberService).create(eq(studyId), eq(testUserId));
        }
    }


    @Nested
    @DisplayName("스터디 생성 실패 시나리오")
    class DateValidationScenarios {
        @Test
        @DisplayName("잘못된 카테고리 입력 시 예외 발생")
        void createStudy_InvalidCategory() {
            validRequestDto.setCategory("INVALID_CATEGORY");

            assertThrows(MosException.class, () -> {
                studyService.create(testUserId, validRequestDto);
            });
        }

        @Test
        @DisplayName("잘못된 진행 방식 입력 시 예외 발생")
        void createStudy_InvalidMeetingType() {
            validRequestDto.setCategory("INVALID_MEETING_TYPE");

            assertThrows(MosException.class, () -> {
                studyService.create(testUserId, validRequestDto);
            });
        }


        @Test
        @DisplayName("모집 종료 날짜가 시작 날짜보다 이전인 경우 예외 발생")
        void createStudy_InvalidRecruitmentDates() {
            // 모집 종료 날짜가 시작 날짜보다 이전인 경우
            validRequestDto.setRecruitmentStartDate(LocalDate.now().plusDays(7));
            validRequestDto.setRecruitmentEndDate(LocalDate.now());

            assertThrows(MosException.class, () -> {
                studyService.create(testUserId, validRequestDto);
            });
        }

    }

    @Test
    @DisplayName("스터디 단 건 조회 성공")
    void getStudy_Success() {

        // given
        Study study = Study.builder()
                .id(1L)
                .title("testStudy")
                .content("testContent")
                .maxStudyMemberCount(6)
                .category(Category.PROGRAMMING)
                .schedule("testSchedule")
                .recruitmentStartDate(LocalDate.now())
                .recruitmentEndDate(LocalDate.now())
                .viewCount(0)
                .meetingType(MeetingType.OFFLINE)
                .tags(StudyTag.fromList(Arrays.asList("testTag1", "testTag2")))
                .requirements("testRequirements")
                .build();

        doNothing().when(studyRepository).increaseViewCount(study.getId());
        when(studyRepository.findById(study.getId())).thenReturn(Optional.of(study));
        // when
        StudyResponseDto studyResponseDto = studyService.get(study.getId());

        // then
        verify(studyRepository).increaseViewCount(study.getId());
        verify(studyRepository).findById(study.getId());
        assertNotNull(studyResponseDto);
    }

    @Test
    @DisplayName("스터디 없는 스터디 아이디로 조회 시 단 건 조회 실패")
    void getStudy_invalidStudyId() {

        // given
        Long studyId = 1L;

        doNothing().when(studyRepository).increaseViewCount(studyId);
        when(studyRepository.findById(studyId)).thenReturn(Optional.empty());

        // when
        MosException mosException = assertThrows(MosException.class, () -> studyService.get(studyId));

        // then
        Assertions.assertThat(mosException.getErrorCode()).isEqualTo(StudyErrorCode.STUDY_NOT_FOUND);
    }
}
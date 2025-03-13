package com.mos.backend.studies.presentation.controller.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mos.backend.common.jwt.TokenUtil;
import com.mos.backend.securityuser.WithMockCustomUser;
import com.mos.backend.studies.application.StudyService;
import com.mos.backend.studies.presentation.dto.StudyCreateRequestDto;
import com.mos.backend.studycurriculum.presentation.requestdto.StudyCurriculumCreateRequestDto;
import com.mos.backend.studyquestions.presentation.requestdto.StudyQuestionCreateRequestDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(StudyController.class)
@AutoConfigureRestDocs(outputDir = "build/generated-snippets")
@AutoConfigureMockMvc(addFilters = false)
@WithMockCustomUser(userId = 1L)
class StudyControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private StudyService studyService;

    @MockitoBean
    private TokenUtil tokenUtil;

    private StudyCreateRequestDto validRequestDto;

    @BeforeEach
    void setUp() {
        validRequestDto = createValidStudyCreateRequestDto();
    }

    private StudyCreateRequestDto createValidStudyCreateRequestDto() {
        StudyCreateRequestDto dto = new StudyCreateRequestDto();
        dto.setTitle("Spring 스터디");
        dto.setCategory("프로그래밍");
        dto.setMaxStudyMemberCount(5);
        dto.setRecruitmentStartDate(LocalDate.now().plusDays(1));
        dto.setRecruitmentEndDate(LocalDate.now().plusDays(7));
        dto.setTags(List.of("Java", "Spring"));
        dto.setMeetingType("대면");
        dto.setContent("Spring Framework를 함께 학습할 스터디원을 모집합니다.");
        dto.setSchedule("매주 화요일 오후 8시");

        // 커리큘럼 추가
        dto.setCurriculums(List.of(
                new StudyCurriculumCreateRequestDto(1L, "Spring 기초", "Spring Framework 기본 개념 학습")
        ));

        // 질문 추가
        dto.setApplicationQuestions(List.of(
                new StudyQuestionCreateRequestDto("개발 경력은 어떻게 되시나요?", 1L, true, "TEXT", List.of("신입", "경력"))
        ));

        dto.setRules(List.of("주 1회 꼭 참석", "과제 미제출 시 벌금"));
        dto.setBenefits(List.of("전문성 향상", "네트워킹"));

        return dto;
    }

    @Test
    @DisplayName("스터디 생성 성공 문서화")
    void createStudy_Success_Documentation() throws Exception {
        // Given
        Long expectedStudyId = 1L;
        when(studyService.create(any(Long.class), any(StudyCreateRequestDto.class)))
                .thenReturn(expectedStudyId);

        // When & Then
        mockMvc.perform(post("/studies")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequestDto)))
                .andExpect(status().isCreated())
                .andDo(document("studies-create-success",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("title").type(JsonFieldType.STRING).description("스터디 제목"),
                                fieldWithPath("category").type(JsonFieldType.STRING).description("스터디 카테고리"),
                                fieldWithPath("maxStudyMemberCount").type(JsonFieldType.NUMBER).description("최대 참여 인원"),
                                fieldWithPath("recruitmentStartDate").type(JsonFieldType.STRING).description("모집 시작 날짜"),
                                fieldWithPath("recruitmentEndDate").type(JsonFieldType.STRING).description("모집 종료 날짜"),
                                fieldWithPath("tags").type(JsonFieldType.ARRAY).description("스터디 태그 목록"),
                                fieldWithPath("meetingType").type(JsonFieldType.STRING).description("모임 방식"),
                                fieldWithPath("content").type(JsonFieldType.STRING).description("스터디 설명"),
                                fieldWithPath("schedule").type(JsonFieldType.STRING).description("스터디 일정"),
                                fieldWithPath("requirements").type(JsonFieldType.VARIES).description("스터디 요구사항 (선택)"),
                                fieldWithPath("curriculums").type(JsonFieldType.ARRAY).description("커리큘럼 목록"),
                                fieldWithPath("curriculums[].sectionId").type(JsonFieldType.NUMBER).description("커리큘럼 섹션 ID"),
                                fieldWithPath("curriculums[].title").type(JsonFieldType.STRING).description("커리큘럼 제목"),
                                fieldWithPath("curriculums[].content").type(JsonFieldType.STRING).description("커리큘럼 내용"),
                                fieldWithPath("rules").type(JsonFieldType.ARRAY).description("스터디 규칙 목록"),
                                fieldWithPath("benefits").type(JsonFieldType.ARRAY).description("스터디 혜택 목록"),
                                fieldWithPath("applicationQuestions").type(JsonFieldType.ARRAY).description("가입 신청 질문 목록"),
                                fieldWithPath("applicationQuestions[].question").type(JsonFieldType.STRING).description("질문 내용"),
                                fieldWithPath("applicationQuestions[].questionNum").type(JsonFieldType.NUMBER).description("질문 ID"),
                                fieldWithPath("applicationQuestions[].required").type(JsonFieldType.BOOLEAN).description("필수 질문 여부"),
                                fieldWithPath("applicationQuestions[].type").type(JsonFieldType.STRING).description("질문 유형"),
                                fieldWithPath("applicationQuestions[].options").type(JsonFieldType.ARRAY).description("질문 옵션")
                        )
                ));
    }
}
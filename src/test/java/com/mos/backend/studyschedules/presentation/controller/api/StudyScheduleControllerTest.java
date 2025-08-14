package com.mos.backend.studyschedules.presentation.controller.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mos.backend.common.jwt.TokenUtil;
import com.mos.backend.common.test.config.TestWebSocketConfig;
import com.mos.backend.securityuser.WithMockCustomUser;
import com.mos.backend.studyschedules.application.StudyScheduleService;
import com.mos.backend.studyschedules.presentation.req.StudyScheduleCreateReq;
import com.mos.backend.studyschedules.presentation.req.StudyScheduleUpdateReq;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Import(TestWebSocketConfig.class)
@WebMvcTest(StudyScheduleController.class)
@AutoConfigureRestDocs(outputDir = "build/generated-snippets")
@AutoConfigureMockMvc(addFilters = false)
@WithMockCustomUser(userId = 1L)
class StudyScheduleControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private StudyScheduleService studyScheduleService;
    @MockitoBean
    private TokenUtil tokenUtil;

    @Test
    @DisplayName("스터디 스케쥴 생성 성공 문서화")
    void createStudySchedule_Success_Documentation() throws Exception {
        mockMvc.perform(
                        post("/studies/{studyId}/schedules", 1L)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(
                                                new StudyScheduleCreateReq(
                                                        List.of(1L, 2L, 3L),
                                                        "스터디 일정 제목",
                                                        "스터디 일정 설명",
                                                        LocalDateTime.now().plusHours(1),
                                                        LocalDateTime.now().plusHours(2))
                                        )
                                )
                )
                .andExpect(status().isOk())
                .andDo(document("study-schedule-create-success",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("studyId").description("스터디 ID")
                        ),
                        requestFields(
                                fieldWithPath("curriculumIds").description("커리큘럼 ID 목록"),
                                fieldWithPath("title").description("일정 제목"),
                                fieldWithPath("description").optional().description("일정 설명"),
                                fieldWithPath("startDateTime").description("시작 일시 (현재 시간 이후)"),
                                fieldWithPath("endDateTime").optional().description("종료 일시")
                        )
                ));
    }


    @Test
    @DisplayName("나의 스터디 스케쥴 목록 조회 성공 문서화")
    void getMyStudySchedules_Success_Documentation() throws Exception {
        mockMvc.perform(
                        get("/study-schedules")
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andDo(document("my-study-schedules-success",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint())
                ));
    }

    @Test
    @DisplayName("스터디 스케쥴 목록 조회 성공 문서화")
    void getStudySchedules_Success_Documentation() throws Exception {
        mockMvc.perform(
                        get("/studies/{studyId}/schedules", 1L)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andDo(document("study-schedules-success",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("studyId").description("스터디 ID")
                        )
                ));
    }

    @Test
    @DisplayName("스터디 스케쥴 수정 성공 문서화")
    void updateStudySchedule_Success_Documentation() throws Exception {
        mockMvc.perform(
                        patch("/studies/{studyId}/study-schedules/{studyScheduleId}", 1L, 1L)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(
                                                new StudyScheduleUpdateReq(
                                                        List.of(1L, 2L, 3L),
                                                        "스터디 일정 제목",
                                                        "스터디 일정 설명",
                                                        LocalDateTime.now().plusHours(1),
                                                        LocalDateTime.now().plusHours(2))
                                        )
                                )
                )
                .andExpect(status().isOk())
                .andDo(document("study-schedule-update-success",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("studyId").description("스터디 ID"),
                                parameterWithName("studyScheduleId").description("스터디 스케쥴 ID")
                        ),
                        requestFields(
                                fieldWithPath("curriculumIds").description("커리큘럼 ID 목록"),
                                fieldWithPath("title").description("일정 제목"),
                                fieldWithPath("description").optional().description("일정 설명"),
                                fieldWithPath("startDateTime").description("시작 일시 (현재 시간 이후)"),
                                fieldWithPath("endDateTime").optional().description("종료 일시")
                        )
                ));
    }

    @Test
    @DisplayName("스터디 스케쥴 삭제 성공 문서화")
    void deleteStudySchedule_Success_Documentation() throws Exception {
        mockMvc.perform(
                        delete("/studies/{studyId}/study-schedules/{studyScheduleId}", 1L, 1L)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andDo(document("study-schedule-delete-success",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("studyId").description("스터디 ID"),
                                parameterWithName("studyScheduleId").description("스터디 스케쥴 ID")
                        )
                ));
    }
}

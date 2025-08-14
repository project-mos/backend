package com.mos.backend.attendances.presentation.controller.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mos.backend.attendances.application.AttendanceService;
import com.mos.backend.common.jwt.TokenUtil;
import com.mos.backend.common.test.config.TestWebSocketConfig;
import com.mos.backend.securityuser.WithMockCustomUser;
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

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Import(TestWebSocketConfig.class)
@WebMvcTest(AttendanceController.class)
@AutoConfigureRestDocs(outputDir = "build/generated-snippets")
@AutoConfigureMockMvc(addFilters = false)
@WithMockCustomUser(userId = 1L)
class AttendanceControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AttendanceService attendanceService;
    @MockitoBean
    private TokenUtil tokenUtil;

    @Test
    @DisplayName("출석 성공 문서화")
    void create_Success_Documentation() throws Exception {
        mockMvc.perform(
                        post("/studies/{studyId}/schedules/{studyScheduleId}/attendances", 1, 1, 1)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andDo(document("create-attendance-success",
                                preprocessRequest(prettyPrint()),
                                preprocessResponse(prettyPrint())
                        )
                );
    }

    @Test
    @DisplayName("출석 수정 성공 문서화")
    void update_Success_Documentation() throws Exception {
        mockMvc.perform(
                        put("/studies/{studyId}/schedules/{studyScheduleId}/attendances", 1, 1, 1)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString("출석"))
                )
                .andExpect(status().isOk())
                .andDo(document("update-attendance-success",
                                preprocessRequest(prettyPrint()),
                                preprocessResponse(prettyPrint())
                        )
                );
    }

    @Test
    @DisplayName("스터디원 출석률 조회 성공 문서화")
    void getStudyMemberAttendances_Success_Documentation() throws Exception {
        mockMvc.perform(
                        get("/studies/{studyId}/members/attendances", 1)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andDo(document("get-study-member-attendances-success",
                                preprocessRequest(prettyPrint()),
                                preprocessResponse(prettyPrint())
                        )
                );
    }

    @Test
    @DisplayName("스터디 일정 출석률 조회 성공 문서화")
    void getStudyScheduleAttendanceRates_Success_Documentation() throws Exception {
        mockMvc.perform(
                        get("/studies/{studyId}/schedules/attendances", 1)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andDo(document("get-study-schedule-attendance-rates-success",
                                preprocessRequest(prettyPrint()),
                                preprocessResponse(prettyPrint())
                        )
                );
    }

    @Test
    @DisplayName("출석 삭제 성공 문서화")
    void delete_Success_Documentation() throws Exception {
        mockMvc.perform(
                        delete("/studies/{studyId}/schedules/{studyScheduleId}/attendances/{attendanceId}", 1, 1, 1, 1)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andDo(document("delete-attendance-success",
                                preprocessRequest(prettyPrint()),
                                preprocessResponse(prettyPrint())
                        )
                );
    }
}

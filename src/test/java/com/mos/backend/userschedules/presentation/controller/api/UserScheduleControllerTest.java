package com.mos.backend.userschedules.presentation.controller.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mos.backend.common.jwt.TokenUtil;
import com.mos.backend.common.test.config.TestWebSocketConfig;
import com.mos.backend.securityuser.WithMockCustomUser;
import com.mos.backend.userschedules.application.UserScheduleService;
import com.mos.backend.userschedules.presentation.req.UserScheduleCreateReq;
import com.mos.backend.userschedules.presentation.req.UserScheduleUpdateReq;
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

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Import(TestWebSocketConfig.class)
@WebMvcTest(UserScheduleController.class)
@AutoConfigureRestDocs(outputDir = "build/generated-snippets")
@AutoConfigureMockMvc(addFilters = false)
@WithMockCustomUser(userId = 1L)
class UserScheduleControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UserScheduleService userScheduleService;
    @MockitoBean
    private TokenUtil tokenUtil;

    @Test
    @DisplayName("유저 일정 생성 성공 문서화")
    void createUserSchedule_Success_Documentation() throws Exception {
        mockMvc.perform(
                        post("/user-schedules")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(
                                                new UserScheduleCreateReq("title", "description", LocalDateTime.now().plusHours(1), LocalDateTime.now().plusHours(2))
                                        )
                                )
                )
                .andExpect(status().isOk())
                .andDo(document("user-schedules-create-success",
                                preprocessRequest(prettyPrint()),
                                preprocessResponse(prettyPrint())
                        )
                );
    }

    @Test
    @DisplayName("유저 일정 목록 조회 성공 문서화")
    void getUserSchedules_Success_Documentation() throws Exception {
        mockMvc.perform(
                        get("/user-schedules")
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andDo(document("user-schedules-success",
                                preprocessRequest(prettyPrint()),
                                preprocessResponse(prettyPrint())
                        )
                );
    }

    @Test
    @DisplayName("유저 일정 수정 성공 문서화")
    void updateUserSchedule_Success_Documentation() throws Exception {
        mockMvc.perform(
                        patch("/user-schedules/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(
                                                new UserScheduleUpdateReq("title", "description", LocalDateTime.now().plusHours(1), LocalDateTime.now().plusHours(2))
                                        )
                                )
                )
                .andExpect(status().isOk())
                .andDo(document("user-schedules-update-success",
                                preprocessRequest(prettyPrint()),
                                preprocessResponse(prettyPrint())
                        )
                );
    }

    @Test
    @DisplayName("유저 일정 삭제 성공 문서화")
    void deleteUserSchedule_Success_Documentation() throws Exception {
        mockMvc.perform(
                        delete("/user-schedules/1")
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andDo(document("user-schedules-delete-success",
                                preprocessRequest(prettyPrint()),
                                preprocessResponse(prettyPrint())
                        )
                );
    }
}

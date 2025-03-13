package com.mos.backend.studyparticipations.presentation.controller.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mos.backend.common.jwt.TokenUtil;
import com.mos.backend.securityuser.WithMockCustomUser;
import com.mos.backend.studyparticipations.application.StudyApplicationService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.put;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(StudyApplicationController.class)
@AutoConfigureRestDocs(outputDir = "build/generated-snippets")
@AutoConfigureMockMvc(addFilters = false)
@WithMockCustomUser(userId = 1L)
class StudyApplicationControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private StudyApplicationService studyApplicationService;
    @MockitoBean
    private TokenUtil tokenUtil;

    @Test
    @DisplayName("스터디 신청 승인 성공 문서화")
    void approveStudyApplication_Success_Documentation() throws Exception {
        mockMvc.perform(
                        put("/study-applications/1/approval")
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andDo(document("study-applications-approve-success",
                                preprocessRequest(prettyPrint()),
                                preprocessResponse(prettyPrint())
                        )
                );
    }

    @Test
    @DisplayName("스터디 신청 거절 성공 문서화")
    void rejectStudyApplication_Success_Documentation() throws Exception {
        mockMvc.perform(
                        put("/study-applications/1/rejection")
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andDo(document("study-applications-reject-success",
                                preprocessRequest(prettyPrint()),
                                preprocessResponse(prettyPrint())
                        )
                );
    }
}

package com.mos.backend.users.presentation.controller.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mos.backend.common.infrastructure.EntityFacade;
import com.mos.backend.common.jwt.TokenUtil;
import com.mos.backend.common.test.config.TestWebSocketConfig;
import com.mos.backend.securityuser.WithMockCustomUser;
import com.mos.backend.studies.application.StudyService;
import com.mos.backend.studies.entity.Category;
import com.mos.backend.users.application.UserService;
import com.mos.backend.users.application.responsedto.UserDetailRes;
import com.mos.backend.users.presentation.requestdto.UserUpdateReq;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.patch;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Import(TestWebSocketConfig.class)
@WebMvcTest(UserController.class)
@AutoConfigureRestDocs(outputDir = "build/generated-snippets")
@AutoConfigureMockMvc(addFilters = false)
@WithMockCustomUser(userId = 1L)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockitoBean
    private StudyService studyService;
    @MockitoBean
    private TokenUtil tokenUtil;
    @MockitoBean
    private UserService userService;
    @MockitoBean
    private EntityFacade entityFacade;

    @Test
    @DisplayName("유저 정보 수정 성공 문서화")
    void updateUser_Success_Documentation() throws Exception {
        // Given
        UserUpdateReq validReq = new UserUpdateReq("nickname", "introduction", List.of(Category.BOOK, Category.PROGRAMMING));

        // When & Then
        mockMvc.perform(patch("/users")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validReq)))
                .andExpect(status().isOk())
                .andDo(document("users-update-success",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("nickname").type(JsonFieldType.STRING).description("닉네임"),
                                fieldWithPath("introduction").type(JsonFieldType.STRING).description("자기소개"),
                                fieldWithPath("categories").type(JsonFieldType.ARRAY).description("카테고리 목록")
                        )
                ));
    }

    @Test
    @DisplayName("유저 정보 조회 성공 문서화")
    void getUserDetail_Success_Documentation() throws Exception {
        // Given
        UserDetailRes userDetailRes = new UserDetailRes("nickname", "introduction", "category1,category2", "imagePath");
        when(userService.getDetail(any(Long.class))).thenReturn(userDetailRes);

        // When & Then
        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andDo(document("users-get-detail-success",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        responseFields(
                                fieldWithPath("nickname").description("유저 닉네임"),
                                fieldWithPath("introduction").description("유저 소개"),
                                fieldWithPath("categories").description("유저가 관심 있는 카테고리 목록 (콤마로 구분)"),
                                fieldWithPath("imagePath").description("유저의 프로필 이미지 경로")
                        )
                ));
    }
}

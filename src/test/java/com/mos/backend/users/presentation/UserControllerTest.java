package com.mos.backend.users.presentation;

import com.mos.backend.users.presentation.controller.api.UserController;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@AutoConfigureRestDocs(outputDir = "build/generated-snippets")
@WithMockUser
class UserControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Nested
    @DisplayName("hello 테스트")
    class helloTests {

        @Test
        @DisplayName("ArticleId가 1이 아니면 정상 응답한다.")
        void helloSuccessifArticleIdNot1Test() throws Exception {
            // given
            Long userId = 2L;

            // when
            ResultActions result = mockMvc.perform(get("/hello/{userId}", userId)
                    .accept(MediaType.APPLICATION_JSON)
            );

            // then
            result.andExpect(status().isOk())
                    .andDo(document("users-get-success",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            pathParameters(
                                    parameterWithName("userId").description("조회할 유저 아이디")
                            ),
                            responseFields(
                                    fieldWithPath("status").description("응답 성공 여부").type(JsonFieldType.STRING),
                                    fieldWithPath("message").description("응답 메시지").type(JsonFieldType.STRING),
                                    fieldWithPath("data").description("조회한 유저 데이터").type(JsonFieldType.OBJECT),
                                    fieldWithPath("data.id").description("조회한 유저 아이디").type(JsonFieldType.NUMBER)
                            )));
        }

        @Test
        @DisplayName("ArticleId가 1이면 에러를 발생한다.")
        void helloFailIfArticleId1Test() throws Exception {
            // given
            Long userId = 1L;

            // when
            ResultActions result = mockMvc.perform(get("/hello/{userId}", userId)
                    .accept(MediaType.APPLICATION_JSON)
            );

            // then
            result.andExpect(status().isNotFound())
                    .andDo(document("users-get-fail",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            pathParameters(
                                    parameterWithName("userId").description("조회할 유저 아이디")
                            ),
                            responseFields(
                                    fieldWithPath("status").description("응답 성공 여부").type(JsonFieldType.STRING),
                                    fieldWithPath("message").description("에러 메시지").type(JsonFieldType.STRING),
                                    fieldWithPath("data").description("데이터").type(JsonFieldType.NULL)
                            )));
        }
    }
}
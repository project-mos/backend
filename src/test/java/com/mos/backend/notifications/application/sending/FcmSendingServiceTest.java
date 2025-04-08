package com.mos.backend.notifications.application.sending;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.firebase.messaging.*;
import com.mos.backend.common.event.EventType;
import com.mos.backend.notifications.application.UserFcmTokenService;
import com.mos.backend.notifications.application.dto.DataPayloadDto;
import com.mos.backend.notifications.entity.UserFcmToken;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;


import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FcmSendingServiceTest {

    @Mock
    private FirebaseMessaging firebaseMessaging;
    @Mock
    private UserFcmTokenService userFcmTokenService;
    private ObjectMapper objectMapper = new ObjectMapper();
    private FcmSendingService fcmSendingService;

    private Long userId = 1L;
    private Long studyId = 1L;
    private String studyName = "studyName";
    private String fileName = "fileName";
    private String title = "title";
    private String content = "content";
    private DataPayloadDto dataPayloadDto;
    private EventType type = EventType.FILE_UPLOADED;

    @BeforeEach
    void setUp() {
        fcmSendingService = new FcmSendingService(firebaseMessaging, userFcmTokenService, objectMapper);
        dataPayloadDto = DataPayloadDto.forFileUpload(type, studyId, studyName, fileName);
        objectMapper = new ObjectMapper();
    }

    @Nested
    @DisplayName("sendMessageTest")
    class sendMessageTest {

        @Test
        @DisplayName("메시지를 성공적으로 발송한다.")
        void sendMessage_Success() throws FirebaseMessagingException {

            // given
            UserFcmToken mockUserFcmToken1 = mock(UserFcmToken.class);
            when(mockUserFcmToken1.getToken()).thenReturn("token1");
            UserFcmToken mockUserFcmToken2 = mock(UserFcmToken.class);
            when(mockUserFcmToken2.getToken()).thenReturn("token2");
            List<UserFcmToken> mockUserFcmTokenList = List.of(mockUserFcmToken1, mockUserFcmToken2);
            when(userFcmTokenService.findByUserId(userId)).thenReturn(mockUserFcmTokenList);

            BatchResponse mockBatchResponse = mock(BatchResponse.class);
            when(mockBatchResponse.getFailureCount()).thenReturn(0);
            when(firebaseMessaging.sendEachForMulticast(any(MulticastMessage.class))).thenReturn(mockBatchResponse);

            // when
            fcmSendingService.sendMessage(userId, title, content,  dataPayloadDto);

            // then
            verify(firebaseMessaging).sendEachForMulticast(any(MulticastMessage.class));
        }

        @Test
        @DisplayName("사용자 토큰이 없으면 FCM 발송 시도를 하지 않는다.")
        void givenNoToken_WhenSendMessage_Nothing() throws FirebaseMessagingException {
            // given
            when(userFcmTokenService.findByUserId(userId)).thenReturn(Collections.EMPTY_LIST);

            // when
            fcmSendingService.sendMessage(userId, title, content,  dataPayloadDto);

            // then
            verify(firebaseMessaging, never()).sendEachForMulticast(any(MulticastMessage.class));
        }

        @Test
        @DisplayName("토큰 문제로 인한 발송 실패 토큰은 삭제 처리한다.")
        void givenInvalidToken_WhenSendMessage_DeleteToken() throws FirebaseMessagingException {

            // given
            UserFcmToken mockUserFcmToken1 = mock(UserFcmToken.class);
            String tokenSuccess = "token_success";
            when(mockUserFcmToken1.getToken()).thenReturn(tokenSuccess);
            UserFcmToken mockUserFcmToken2 = mock(UserFcmToken.class);
            String tokenFailUnregistered = "token_fail_unregistered";
            when(mockUserFcmToken2.getToken()).thenReturn(tokenFailUnregistered);
            UserFcmToken mockUserFcmToken3 = mock(UserFcmToken.class);
            String tokenFailInternal = "token_fail_internal";
            when(mockUserFcmToken3.getToken()).thenReturn(tokenFailInternal);
            List<UserFcmToken> mockUserFcmTokenList = List.of(mockUserFcmToken1, mockUserFcmToken2, mockUserFcmToken3);
            when(userFcmTokenService.findByUserId(userId)).thenReturn(mockUserFcmTokenList);

            SendResponse successResponse = mock(SendResponse.class);
            when(successResponse.isSuccessful()).thenReturn(true);

            SendResponse failResponseUnregistered = mock(SendResponse.class);
            when(failResponseUnregistered.isSuccessful()).thenReturn(false);
            FirebaseMessagingException unregisteredException = mock(FirebaseMessagingException.class);
            when(unregisteredException.getMessagingErrorCode()).thenReturn(MessagingErrorCode.UNREGISTERED);
            when(failResponseUnregistered.getException()).thenReturn(unregisteredException);

            SendResponse failResponseInternal = mock(SendResponse.class);
            when(failResponseInternal.isSuccessful()).thenReturn(false);
            FirebaseMessagingException internalException = mock(FirebaseMessagingException.class);
            when(internalException.getMessagingErrorCode()).thenReturn(MessagingErrorCode.INTERNAL);
            when(failResponseInternal.getException()).thenReturn(internalException);

            BatchResponse mockBatchResponse = mock(BatchResponse.class);
            when(mockBatchResponse.getFailureCount()).thenReturn(2);
            when(mockBatchResponse.getResponses()).thenReturn(List.of(successResponse, failResponseUnregistered, failResponseInternal));

            when(firebaseMessaging.sendEachForMulticast(any(MulticastMessage.class))).thenReturn(mockBatchResponse);

            // when
            fcmSendingService.sendMessage(userId, title, content,  dataPayloadDto);

            // then
            verify(firebaseMessaging, times(1)).sendEachForMulticast(any(MulticastMessage.class));
            verify(userFcmTokenService, times(1)).delete(userId, tokenFailUnregistered);

        }
    }
}
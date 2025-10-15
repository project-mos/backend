package com.mos.backend.notifications.application.sending;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.firebase.messaging.*;
import com.mos.backend.notifications.application.UserFcmTokenService;
import com.mos.backend.notifications.application.dto.payload.DataPayload;
import com.mos.backend.notifications.application.dto.payload.StudyFileUploadPayload;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FcmSendingServiceTest {

    @Mock
    private FirebaseMessaging firebaseMessaging;
    @Mock
    private UserFcmTokenService userFcmTokenService;

    @Spy
    private ObjectMapper objectMapper = new ObjectMapper();

    @InjectMocks
    private FcmSendingService fcmSendingService;

    private String title = "테스트 제목";
    private String content = "테스트 내용";
    private DataPayload dataPayload;

    @BeforeEach
    void setUp() {
        dataPayload = StudyFileUploadPayload.success(1L, "스터디", "파일.pdf");
    }

    @Test
    @DisplayName("여러 수신자에게 성공적으로 멀티캐스트 메시지를 발송한다.")
    void sendMulticastMessage_Success() throws FirebaseMessagingException {
        // given
        List<Long> recipientIds = List.of(1L, 2L);
        Map<String, Long> tokenMap = Map.of(
                "token-user1", 1L,
                "token-user2", 2L
        );
        when(userFcmTokenService.findTokensByUserIds(recipientIds)).thenReturn(tokenMap);

        BatchResponse mockBatchResponse = mock(BatchResponse.class);
        when(mockBatchResponse.getFailureCount()).thenReturn(0);
        when(firebaseMessaging.sendEachForMulticast(any(MulticastMessage.class))).thenReturn(mockBatchResponse);

        // when
        fcmSendingService.sendMulticastMessage(recipientIds, title, content, dataPayload);

        // then
        verify(firebaseMessaging, times(1)).sendEachForMulticast(any(MulticastMessage.class));
    }

    @Test
    @DisplayName("수신자들의 토큰이 하나도 없으면 FCM 발송을 시도하지 않는다.")
    void sendMulticastMessage_WhenNoTokens_ShouldNotSend() throws FirebaseMessagingException {
        // given
        List<Long> recipientIds = List.of(1L, 2L);
        when(userFcmTokenService.findTokensByUserIds(recipientIds)).thenReturn(Collections.emptyMap());

        // when
        fcmSendingService.sendMulticastMessage(recipientIds, title, content, dataPayload);

        // then
        verify(firebaseMessaging, never()).sendEachForMulticast(any(MulticastMessage.class));
    }

    @Test
    @DisplayName("잘못된 토큰(UNREGISTERED)으로 발송 실패 시, 해당 유저의 토큰만 삭제 요청한다.")
    void sendMulticastMessage_WithInvalidToken_ShouldDeleteCorrectToken() throws FirebaseMessagingException {
        // given
        Long user1Id = 1L;
        Long user2Id = 2L;
        String user1Token = "token-user1-success";
        String user2TokenInvalid = "token-user2-invalid";

        List<Long> recipientIds = List.of(user1Id, user2Id);
        Map<String, Long> tokenMap = new LinkedHashMap<>();
        tokenMap.put(user1Token, user1Id);
        tokenMap.put(user2TokenInvalid, user2Id);

        when(userFcmTokenService.findTokensByUserIds(recipientIds)).thenReturn(tokenMap);

        // FCM 응답 Mocking
        SendResponse successResponse = mock(SendResponse.class);
        when(successResponse.isSuccessful()).thenReturn(true);

        SendResponse failResponse = mock(SendResponse.class);
        when(failResponse.isSuccessful()).thenReturn(false);
        FirebaseMessagingException fme = mock(FirebaseMessagingException.class);
        when(fme.getMessagingErrorCode()).thenReturn(MessagingErrorCode.UNREGISTERED);
        when(failResponse.getException()).thenReturn(fme);

        BatchResponse mockBatchResponse = mock(BatchResponse.class);
        when(mockBatchResponse.getFailureCount()).thenReturn(1);
        when(mockBatchResponse.getResponses()).thenReturn(List.of(successResponse, failResponse));

        when(firebaseMessaging.sendEachForMulticast(any(MulticastMessage.class))).thenReturn(mockBatchResponse);

        // when
        fcmSendingService.sendMulticastMessage(recipientIds, title, content, dataPayload);

        // then
        verify(firebaseMessaging, times(1)).sendEachForMulticast(any(MulticastMessage.class));
        // user2의 잘못된 토큰만 삭제를 요청했는지 정확히 검증
        verify(userFcmTokenService, times(1)).delete(user2Id, user2TokenInvalid);
        // user1의 토큰은 삭제를 요청하지 않았는지 검증
        verify(userFcmTokenService, never()).delete(user1Id, user1Token);
    }

    @Test
    @DisplayName("토큰이 500개를 초과할 경우, 500개씩 나누어 여러 번 발송한다.")
    void sendMulticastMessage_WhenTokensExceedLimit_ShouldSendInBatches() throws FirebaseMessagingException {
        // given
        // 700명의 유저 ID와 토큰을 생성
        List<Long> recipientIds = IntStream.rangeClosed(1, 700).asLongStream().boxed().collect(Collectors.toList());
        Map<String, Long> largeTokenMap = new HashMap<>();
        recipientIds.forEach(id -> largeTokenMap.put("token-" + id, id));

        when(userFcmTokenService.findTokensByUserIds(recipientIds)).thenReturn(largeTokenMap);

        BatchResponse mockBatchResponse = mock(BatchResponse.class);
        when(mockBatchResponse.getFailureCount()).thenReturn(0);
        when(firebaseMessaging.sendEachForMulticast(any(MulticastMessage.class))).thenReturn(mockBatchResponse);

        // when
        fcmSendingService.sendMulticastMessage(recipientIds, title, content, dataPayload);

        // then
        // 700개의 토큰은 500개, 200개로 나뉘어 총 2번 호출되어야 함
        verify(firebaseMessaging, times(2)).sendEachForMulticast(any(MulticastMessage.class));
    }
}
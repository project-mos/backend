package com.mos.backend.notifications.application.sending;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.firebase.messaging.*;
import com.mos.backend.notifications.application.UserFcmTokenService;
import com.mos.backend.notifications.application.dto.payload.DataPayload;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class FcmSendingService implements SendingService {

    private final FirebaseMessaging firebaseMessaging;
    private final UserFcmTokenService userFcmTokenService;
    private final ObjectMapper objectMapper;

    private static final int FCM_MULTICAST_LIMIT = 500;

    /**
     *
     * @param recipientIds  push 받을 유저 id 목록
     * @param title         push 제목
     * @param body          push 본문
     * @param dataPayload   push payload
     */
    @Override
    public void sendMulticastMessage(List<Long> recipientIds, String title, String body, DataPayload dataPayload) {
        // 모든 수신자의 토큰과 사용자 ID 매핑 정보 조회
        Map<String, Long> tokenToUserIdMap = userFcmTokenService.findTokensByUserIds(recipientIds);

        if (tokenToUserIdMap.isEmpty()) {
            log.warn("[FCM Send] no FCM token for users {}", recipientIds);
            return;
        }

        List<String> allTokens = new ArrayList<>(tokenToUserIdMap.keySet());

        // 500개 씩 나누어서 메시지 발송
        for (int i = 0; i < allTokens.size(); i += FCM_MULTICAST_LIMIT) {
            int end = Math.min(i + FCM_MULTICAST_LIMIT, allTokens.size());
            List<String> tokenSublist = allTokens.subList(i, end);
            sendMessageChunk(tokenSublist, title, body, dataPayload, tokenToUserIdMap);
        }
    }

    /**
     * 최대 500개의 토큰을 가지고 FCM 메시지를 발송하는 메서드
     */
    private void sendMessageChunk(List<String> tokenChunk, String title, String body,
                                  DataPayload dataPayload, Map<String, Long> tokenToUserIdMap) {
        Notification notificationPayload = Notification.builder()
                .setTitle(title)
                .setBody(body)
                .build();

        Map<String, String> dataMap = convertPayloadToStringMap(dataPayload);

        MulticastMessage message = MulticastMessage.builder()
                .setNotification(notificationPayload)
                .putAllData(dataMap)
                .addAllTokens(tokenChunk)
                .build();

        try {
            BatchResponse batchResponse = firebaseMessaging.sendEachForMulticast(message);
            if (batchResponse.getFailureCount() > 0) {
                handleFailedTokens(batchResponse, tokenChunk, tokenToUserIdMap);
            }
            log.info("[FCM Send Chunk] Success: {}, Failure: {}",
                    batchResponse.getSuccessCount(), batchResponse.getFailureCount());
        } catch (FirebaseMessagingException e) {
            log.error("[FCM Send Chunk] Error sending message chunk: {}", e.getMessage(), e);
        }
    }

    // dto를 key -> value의 Map으로 변환
    private Map<String, String> convertPayloadToStringMap(DataPayload dataPayload) {
        if (dataPayload == null) return Map.of();
        return objectMapper.convertValue(dataPayload, new TypeReference<Map<String, String>>() {});
    }

    /**
     * 발송에 실패한 토큰들을 식별하고 DB에서 삭제 처리
     * @param response          FCM 발송 결과
     * @param originalTokens    보냈던 전체 토큰 목록
     * @param tokenToUserIdMap  어떤 토큰이 어떤 유저의 것인지에 대한 매핑 정보
     */
    private void handleFailedTokens(BatchResponse response, List<String> originalTokens, Map<String, Long> tokenToUserIdMap) {
        List<SendResponse> responseList = response.getResponses();
        List<String> tokensToDelete = new ArrayList<>();

        for (int i = 0; i < responseList.size(); i++) {
            if (!responseList.get(i).isSuccessful()) {
                String failedToken = originalTokens.get(i);
                if (responseList.get(i).getException() instanceof FirebaseMessagingException fme) {
                    MessagingErrorCode errorCode = fme.getMessagingErrorCode();
                    if (MessagingErrorCode.UNREGISTERED.equals(errorCode) || MessagingErrorCode.INVALID_ARGUMENT.equals(errorCode)) {
                        tokensToDelete.add(failedToken);
                        log.warn("[FCM Cleanup] Token {} will be deleted due to error: {}", failedToken, errorCode);
                    }
                }
            }
        }
        for (String token : tokensToDelete) {
            Long userId = tokenToUserIdMap.get(token);
            if (userId != null) {
                userFcmTokenService.delete(userId, token);
            } else {
                log.error("[FCM Cleanup] Could not find user for token: {}", token);
            }
        }
        log.info("[FCM Cleanup] Deleted {} invalid tokens from DB.", tokensToDelete.size());
    }
}

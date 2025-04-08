package com.mos.backend.notifications.application.sending;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.firebase.messaging.*;
import com.mos.backend.notifications.application.UserFcmTokenService;
import com.mos.backend.notifications.entity.UserFcmToken;
import com.mos.backend.notifications.application.dto.DataPayloadDto;
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

    @Override
    public void sendMessage(Long userId, String title, String body, DataPayloadDto dataPayloadDto) {
        List<UserFcmToken> userFcmTokenList = userFcmTokenService.findByUserId(userId);
        List<String> tokenList = userFcmTokenList.stream().map(UserFcmToken::getToken).toList();

        if (tokenList.isEmpty()) {
            log.warn("[FCM Send] no FCM token for user {}", userId);
            return;
        }

        Notification notificationPayload = Notification.builder()
                .setTitle(title)
                .setBody(body)
                .build();

        Map<String, String> dataPayload = convertDtoToStringMap(dataPayloadDto);

        MulticastMessage message = MulticastMessage.builder()
                .setNotification(notificationPayload)
                .putAllData(dataPayload)
                .addAllTokens(tokenList)
                .build();

        try {
            BatchResponse batchResponse = firebaseMessaging.sendEachForMulticast(message);

            if (batchResponse.getFailureCount() > 0) {
                handleFailedTokens(batchResponse, tokenList, userId);
            }
        } catch (FirebaseMessagingException e) {
            log.error("[FCM Send] Error Sending FCM message to user {}: {}", userId, e.getMessage(), e);
        }
    }

    private Map<String, String> convertDtoToStringMap(DataPayloadDto dto) {
        if (dto == null) return Map.of();
        return objectMapper.convertValue(dto, new TypeReference<Map<String, String>>() {});
    }

    private void handleFailedTokens(BatchResponse response, List<String> originalTokens, long userId) {
        List<SendResponse> responseList = response.getResponses();
        List<String> tokensToDelete = new ArrayList<>();
        for (int i = 0; i < responseList.size(); i++) {
            if (!responseList.get(i).isSuccessful()) {
                String failedToken = originalTokens.get(i);
                if (responseList.get(i).getException() instanceof FirebaseMessagingException fme) {
                    MessagingErrorCode errorCode = fme.getMessagingErrorCode();
                    if (MessagingErrorCode.UNREGISTERED.equals(errorCode) || MessagingErrorCode.INVALID_ARGUMENT.equals(errorCode)) {
                        tokensToDelete.add(failedToken);
                    }
                }
            }
        }
        for (String s : tokensToDelete) {
            System.out.println("s = " + s);
        }
        tokensToDelete.forEach(token -> userFcmTokenService.delete(userId, token));
    }
}

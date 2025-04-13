package com.mos.backend.common.jwt;

import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationInterceptor implements ChannelInterceptor {

    private final TokenUtil tokenUtil;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);

        switch (accessor.getCommand()) {
            case CONNECT:
                handleConnectCommand(accessor);
            case SEND:
                handleSendCommand(accessor);
        }

        return message;
    }

    private void handleConnectCommand(StompHeaderAccessor accessor) {
        validateToken(accessor);
    }

    private Long validateToken(StompHeaderAccessor accessor) {
        String accessToken = tokenUtil.extractAccessToken(accessor);
        DecodedJWT decodedJWT = tokenUtil.decodedJWT(accessToken);
        return getUserId(decodedJWT);
    }

    private void handleSendCommand(StompHeaderAccessor accessor) {
        Long userId = validateToken(accessor);

        addUserIdInSession(accessor, userId);
    }

    private static Long getUserId(DecodedJWT decodedJWT) {
        return decodedJWT.getClaim("id").asLong();
    }

    private static void addUserIdInSession(StompHeaderAccessor accessor, Long userId) {
        accessor.getSessionAttributes().put("userId", userId);
    }
}
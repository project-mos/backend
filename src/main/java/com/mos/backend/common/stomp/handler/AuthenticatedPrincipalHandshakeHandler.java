package com.mos.backend.common.stomp.handler;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.mos.backend.common.exception.MosException;
import com.mos.backend.common.jwt.TokenUtil;
import com.mos.backend.users.entity.exception.UserErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

import java.security.Principal;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Component
public class AuthenticatedPrincipalHandshakeHandler extends DefaultHandshakeHandler {
    private final TokenUtil tokenUtil;

    @Override
    protected Principal determineUser(ServerHttpRequest request, WebSocketHandler wsHandler, Map<String, Object> attributes) {
        if (request instanceof ServletServerHttpRequest servletRequest) {
            HttpServletRequest httpServletRequest = servletRequest.getServletRequest();
            Long userId = getVerifiedUserId(httpServletRequest);
            return () -> String.valueOf(userId);
        }

        return null;
    }

    private Long getVerifiedUserId(HttpServletRequest request) {
        try {
            return verifyAccessToken(request);
        } catch (JWTVerificationException e1) {
            try {
                return verifyRefreshToken(request);
            } catch (JWTVerificationException e2) {
                throw new MosException(UserErrorCode.USER_UNAUTHORIZED);
            }
        }
    }

    private Long verifyAccessToken(HttpServletRequest request) {
        String accessToken = tokenUtil.extractAccessToken(request);
        Long userId = tokenUtil.verifyAccessToken(accessToken);
        return userId;
    }

    private Long verifyRefreshToken(HttpServletRequest request) {
        String refreshToken = tokenUtil.extractRefreshToken(request);
        Long userId = tokenUtil.verifyRefreshToken(refreshToken);
        return userId;
    }
}
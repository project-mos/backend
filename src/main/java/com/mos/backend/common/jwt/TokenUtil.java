package com.mos.backend.common.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.mos.backend.common.entity.TokenType;
import com.mos.backend.common.exception.MosException;
import com.mos.backend.common.redis.RedisTokenUtil;
import com.mos.backend.users.entity.exception.UserErrorCode;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Arrays;
import java.util.Date;
import java.util.Optional;

@Slf4j
@Getter
@Component
@RequiredArgsConstructor
public class TokenUtil {
    @Value("${jwt.secret}")
    private String secretKey;
    @Value("${jwt.access.expiration}")
    private Long accessTokenExpirationPeriod;
    @Value("${jwt.refresh.expiration}")
    private Long refreshTokenExpirationPeriod;
    @Value("${jwt.access.cookie-name}")
    private String accessCookieName;
    @Value("${jwt.refresh.cookie-name}")
    private String refreshCookieName;

    private static final String ACCESS_TOKEN_SUBJECT = "AccessToken";
    private static final String REFRESH_TOKEN_SUBJECT = "RefreshToken";
    private static final String BEARER = "Bearer ";

    private final RedisTokenUtil redisTokenUtil;

    public String issueAccessToken(Long memberId) {
        return JWT.create()
                .withSubject(ACCESS_TOKEN_SUBJECT)
                .withClaim("id", memberId)
                .withExpiresAt(new Date(System.currentTimeMillis() + accessTokenExpirationPeriod))
                .sign(Algorithm.HMAC512(secretKey));
    }

    public String issueRefreshToken(Long memberId) {
        String refreshToken = JWT.create()
                .withSubject(REFRESH_TOKEN_SUBJECT)
                .withClaim("id", memberId)
                .withExpiresAt(new Date(System.currentTimeMillis() + accessTokenExpirationPeriod))
                .sign(Algorithm.HMAC512(secretKey));
        saveRefreshToken(memberId, refreshToken);
        return refreshToken;
    }

    private void saveRefreshToken(Long memberId, String refreshToken) {
        redisTokenUtil.setRefreshTokenWithExpire(refreshToken, memberId, Duration.ofDays(refreshTokenExpirationPeriod));
    }

    public String reissueAccessToken(String refreshToken) {
        DecodedJWT decodedJWT;
        try {
            decodedJWT = JWT.require(Algorithm.HMAC512(secretKey)).build().verify(refreshToken);
        } catch (JWTVerificationException e) {
            throw new JWTVerificationException(e.getMessage());
        }

        Long memberId = decodedJWT.getClaim("id").asLong();

        return issueAccessToken(memberId);
    }

    public String extractAccessToken(HttpServletRequest request) {
        Optional<String> requestToken = Optional.ofNullable(request.getHeader(accessCookieName))
                .filter(token -> token.startsWith(BEARER))
                .map(token -> token.substring(7));

        return requestToken.orElse(null);
    }

    public String extractAccessToken(StompHeaderAccessor accessor) {
        Optional<String> requestToken = Optional.ofNullable(accessor.getFirstNativeHeader(accessCookieName))
                .filter(token -> token.startsWith(BEARER))
                .map(token -> token.substring(7));

        return requestToken.orElse(null);
    }

    public String extractRefreshToken(HttpServletRequest request) {
        String cookieName = refreshCookieName;

        Cookie[] cookies = request.getCookies();

        if (cookies == null)
            throw new MosException(UserErrorCode.MISSING_ACCESS_TOKEN);

        return Arrays.stream(cookies)
                .filter(cookie -> cookie.getName().equals(cookieName))
                .map(Cookie::getValue)
                .findFirst()
                .orElseThrow(() -> new MosException(UserErrorCode.MISSING_ACCESS_TOKEN));
    }

    public DecodedJWT decodedJWT(String accessToken) {
        try {
            return JWT.require(Algorithm.HMAC512(secretKey)).build().verify(accessToken);
        } catch (TokenExpiredException e) {
            log.debug("AccessToken is expired: ${}", accessToken);
            throw new MosException(UserErrorCode.EXPIRED_ACCESS_TOKEN);
        } catch (JWTVerificationException e) {
            log.debug("AccessToken is invalid: ${}", accessToken);
            throw new MosException(UserErrorCode.INVALID_ACCESS_TOKEN);
        }
    }

    public void addTokenToCookie(HttpServletResponse response, Long memberId) {
        String accessToken = issueAccessToken(memberId);
        String refreshToken = issueRefreshToken(memberId);

        addCookie(response, TokenType.ACCESS_TOKEN, accessToken, accessTokenExpirationPeriod.intValue());
        addCookie(response, TokenType.REFRESH_TOKEN, refreshToken, refreshTokenExpirationPeriod.intValue());
    }

    private void addCookie(HttpServletResponse response, TokenType tokenType, String tokenValue, int expiration) {
        String tokenName = tokenType == TokenType.ACCESS_TOKEN ? accessCookieName : refreshCookieName;
        Cookie tokenCookie = new Cookie(tokenName, tokenValue);
        tokenCookie.setHttpOnly(true);
        tokenCookie.setPath("/");
        tokenCookie.setMaxAge(expiration);
        response.addCookie(tokenCookie);
    }
}

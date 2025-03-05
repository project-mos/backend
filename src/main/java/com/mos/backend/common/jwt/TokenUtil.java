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
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Arrays;
import java.util.Date;

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
    @Value("${jwt.access.cookie}")
    private String accessCookie;
    @Value("${jwt.refresh.cookie}")
    private String refreshCookie;

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

    public String extractToken(HttpServletRequest request, TokenType tokenType) {
        String cookieName = (tokenType == TokenType.ACCESS_TOKEN) ? accessCookie : refreshCookie;

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
            throw new MosException(UserErrorCode.USER_UNAUTHORIZED);
        }
    }

    public void addTokenToCookie(HttpServletResponse response, Long memberId) {
        String accessToken = issueAccessToken(memberId);
        String refreshToken = issueRefreshToken(memberId);

        addCookie(response, accessCookie, accessToken, accessTokenExpirationPeriod.intValue());
        addCookie(response, refreshCookie, refreshToken, refreshTokenExpirationPeriod.intValue());
    }


    private void addCookie(HttpServletResponse response, String tokenName, String tokenValue, Integer expiration) {
        Cookie tokenCookie = new Cookie(tokenName, tokenValue);
        tokenCookie.setHttpOnly(true);
        tokenCookie.setPath("/");
        tokenCookie.setMaxAge(expiration);
        response.addCookie(tokenCookie);
    }

}

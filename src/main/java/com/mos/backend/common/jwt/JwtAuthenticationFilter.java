package com.mos.backend.common.jwt;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.mos.backend.common.entity.TokenType;
import com.mos.backend.common.exception.MosException;
import com.mos.backend.users.entity.exception.UserErrorCode;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.PatternMatchUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final TokenUtil tokenUtil;

    public static final String[] whitelist = {
            "/oauth**", // oauth
            "/resources/**", "/favicon.ico", // resource
    };

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return PatternMatchUtils.simpleMatch(whitelist, request.getRequestURI());
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String token = tokenUtil.extractToken(request, TokenType.ACCESS_TOKEN);

        if (token == null)
            throw new MosException(UserErrorCode.MISSING_ACCESS_TOKEN);

        DecodedJWT decodedJWT = tokenUtil.decodedJWT(token);
        Long id = decodedJWT.getClaim("id").asLong();
        Authentication authentication = new UsernamePasswordAuthenticationToken(id, null);

        SecurityContextHolder.getContext().setAuthentication(authentication);
        doFilter(request, response, filterChain);
    }
}

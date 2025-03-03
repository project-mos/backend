package com.mos.backend.users.application;

import com.mos.backend.common.jwt.TokenUtil;
import com.mos.backend.users.application.param.OauthParams;
import com.mos.backend.users.application.responsedto.OauthMemberInfo;
import com.mos.backend.users.entity.OauthProvider;
import com.mos.backend.users.entity.User;
import com.mos.backend.users.infrastructure.respository.UserRepositoryImpl;
import com.mos.backend.users.presentation.requestdto.OauthLoginReq;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class OauthService {
    private final RequestOauthInfoService requestOauthInfoService;
    private final TokenUtil tokenUtil;
    private final UserRepositoryImpl userRepositoryImpl;

    public void login(OauthLoginReq req, HttpServletResponse response) {
        OauthParams oauthParam = createOauthParams(req);

        OauthMemberInfo oauthMemberInfo = requestOauthInfoService.request(oauthParam);
        Optional<User> byOauthProviderAndSocialId = userRepositoryImpl.findByOauthProviderAndSocialId(oauthMemberInfo.getOauthProvider(), oauthMemberInfo.getSocialId());

        User user = byOauthProviderAndSocialId.orElseGet(() -> userRepositoryImpl.save(new User(oauthMemberInfo)));

        String accessToken = tokenUtil.issueAccessToken(user.getId());
        String refreshToken = tokenUtil.issueRefreshToken(user.getId());

        addCookie(response, "accessToken", accessToken);
        addCookie(response, "refreshToken", refreshToken);
    }

    private void addCookie(HttpServletResponse response, String tokenName, String tokenValue) {
        Cookie tokenCookie = new Cookie(tokenName, tokenValue);
        tokenCookie.setHttpOnly(true);
        tokenCookie.setPath("/");
        tokenCookie.setMaxAge(1 * 60 * 60);
        response.addCookie(tokenCookie);
    }

    private static OauthParams createOauthParams(OauthLoginReq req) {
        OauthProvider oauthProvider = req.getOauthProvider();
        OauthParams oauthParam = oauthProvider.getOauthParams(req.getCode());
        return oauthParam;
    }

}

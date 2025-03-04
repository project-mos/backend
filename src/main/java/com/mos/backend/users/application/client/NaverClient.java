package com.mos.backend.users.application.client;

import com.mos.backend.common.exception.MosException;
import com.mos.backend.users.application.param.OauthParams;
import com.mos.backend.users.application.responsedto.NaverMemberInfo;
import com.mos.backend.users.application.responsedto.NaverToken;
import com.mos.backend.users.application.responsedto.OauthMemberInfo;
import com.mos.backend.users.entity.OauthProvider;
import com.mos.backend.users.entity.exception.UserErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Component
public class NaverClient implements OauthClient {
    @Value("${oauth.naver.token_url}")
    private String token_url;
    @Value("${oauth.naver.user_url}")
    private String user_url;
    @Value("${oauth.naver.grant_type}")
    private String grant_type;
    @Value("${oauth.naver.client_id}")
    private String client_id;
    @Value("${oauth.naver.client_secret}")
    private String client_secret;
    @Value("${oauth.naver.redirect_uri}")
    private String redirect_uri;

    @Override
    public OauthProvider oauthProvider() {
        return OauthProvider.NAVER;
    }

    @Override
    public String getOauthLoginToken(OauthParams oauthParams) {
        String url = token_url;
        log.debug("Authorization code: " + oauthParams.getAuthorizationCode());

        RestTemplate rt = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> body = oauthParams.makeBody();
        body.add("grant_type", grant_type);
        body.add("client_id", client_id);
        body.add("client_secret", client_secret);
        body.add("redirect_uri", redirect_uri);

        HttpEntity<MultiValueMap<String, String>> tokenRequest = new HttpEntity<>(body, headers);
        log.debug("Current httpEntity state: " + tokenRequest);

        NaverToken naverToken = rt.postForObject(url, tokenRequest, NaverToken.class);
        log.debug("accessToken: " + naverToken);

        if (naverToken == null) {
            log.error("naver token을 정상적으로 가져오지 못했습니다.");
            throw new MosException(UserErrorCode.USER_FORBIDDEN);
        }
        return naverToken.getAccess_token();
    }

    @Override
    public OauthMemberInfo getMemberInfo(String accessToken) {
        String url = user_url;

        RestTemplate rt = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.add("Authorization", "Bearer " + accessToken);

        HttpEntity<String> memberInfoRequest = new HttpEntity<>(headers);

        return rt.postForObject(url, memberInfoRequest, NaverMemberInfo.class);
    }
}

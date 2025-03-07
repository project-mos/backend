package com.mos.backend.users.application.param;

import com.mos.backend.users.entity.OauthProvider;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

@Getter
@AllArgsConstructor
public class KakaoParams implements OauthParams {
    private String authorizationCode;

    @Override
    public OauthProvider oauthProvider() {
        return OauthProvider.KAKAO;
    }

    @Override
    public String getAuthorizationCode() {
        return authorizationCode;
    }

    @Override
    public MultiValueMap<String, String> makeBody() {
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("code", authorizationCode);
        return body;
    }
}

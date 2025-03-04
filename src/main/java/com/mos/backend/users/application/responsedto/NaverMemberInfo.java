package com.mos.backend.users.application.responsedto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.mos.backend.users.entity.OauthProvider;
import lombok.Getter;

@JsonIgnoreProperties(ignoreUnknown = true)
public class NaverMemberInfo implements OauthMemberInfo {
    @JsonProperty("response")
    private Response response;

    @Override
    public String getSocialId() {
        return response.id;
    }

    @Override
    public String getEmail() {
        return response.email;
    }

    @Override
    public String getNickname() {
        return response.nickname;
    }

    @Override
    public OauthProvider getOauthProvider() {
        return OauthProvider.NAVER;
    }

    @Getter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public class Response {
        private String id;
        private String nickname;
        private String email;
    }
}
